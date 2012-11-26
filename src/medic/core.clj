(ns medic.core
	(:gen-class :main true)
	(:use org.satta.glob)
	(:require [medic.pre :as pre])
	(:require [clojure.java.io :as io])
	(:use jsoup.soup)
	(:use [clojure.tools.cli :only [cli]])
	(:import [com.petebevin.markdown MarkdownProcessor])
	(:import [java.io File]))

; keep the options as a ref available
(def options (ref []))
; should be as an option
(def file-regexp "/**/*.md")

(defn path-to-toc[]
	(str (@options :output) "/" (@options :toc-filename)))

(defn write
	"Helper method. Writes/Append to file"
	([text dontappend] (spit (path-to-toc) text))
	([text] (spit (path-to-toc) text :append true)))

(defn markup-to-html
	"turn markup into html"
	[filepath]
	(.markdown (MarkdownProcessor.) (slurp filepath)))

(defn anchorify
	"wrap a tag with a name anchor"
	[htag]
	(let[sanity (.replaceAll (.html htag) " " "")]
	(.html htag 
		(str 
		 "<" (.tagName htag) ">" 
		 (.html htag) 
		 "</" (.tagName htag) ">" 
		 "</a>"))
	(.tagName htag "a")
	(.attr htag "name" sanity)))	

(defn anchors
	"wrap all header tags with name anchors"
	[content]
	(doseq [t (map #(str "h" %) (range 1 7))]
		(pmap #(anchorify %) (select t content)))
	content)

(defn html-with-anchors 
	"Parse a file, turn it to HTML and spit the clean content to a file"
	[parsed]
	(select "body > *" (anchors (.clone parsed))))

(defn path-to-html-output
	"Find the path to output html version of a markup file"
	[filepath]
	(str 
		(@options :output) 
		"/" 
		(.getName (io/file filepath)) 
		".html"))

(defn parse-file[filepath]
	(parse (markup-to-html filepath)))

(defn toc-one
	"Remove all the tags except header tags to keep doc structure"
	[content]
	(doseq [t ["p" "ul" "li" "a" "img" "pre" "code" "blockquote" ]]
		(.remove (select t content)))
	($ content "body > *"))

(defn process-content
	"Process the content of a markup file"
	[markup-file]
	(let [ parsed (parse-file markup-file)] 
	 ; write html to file
 	 (spit (path-to-html-output markup-file) (html-with-anchors parsed))
 	 ; write toc to file
     (write (toc-one parsed))))

(defn toc-files
	"Process all <files>"
	[files]
	(doseq [markup-file files] 
		  (process-content markup-file)))

(defn toc-folder
	"Prepare a TOC from files found in a folder"
	[base]
	(toc-files (glob (str base file-regexp))))

(defn toc
	"Main method"
	[base] 
	; clean up previous file
	(write "" false)
	(if (@options :customization)
	 (write (slurp (str (@options :customization) "/header.html"))))
	(toc-folder base)
	(if (@options :customization)
	 (write (slurp (str (@options :customization) "/footer.html")))))

(defn -main
	"Main method. Will be called from the command line"
	[& args]
	(let [
		[loptions args banner]  
			(cli args
				["-h" "--help" "Print this message"]
     			["-o" "--output" "Output folder" :default "output"] 
     			["-toc" "--toc-filename" "TOC filename" :default "toc.html"]
     			["-d" "--folder" "The top folder with the markdown files" :default "text"]
     			["-c" "--customization" "A folder with header.html, footer.html"])]
	(if (contains? loptions :help)
		(println banner)
		(do
			(dosync (ref-set options loptions))
			(println "Using parameters:" @options)
			(toc (@options :folder))))
	(System/exit 0)))