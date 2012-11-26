(ns medic.core
	(:gen-class :main true)
	(:use org.satta.glob)
	(:require [clojure.java.io :as io])
	(:use jsoup.soup)
	(:use [clojure.tools.cli :only [cli]])
	(:import [com.petebevin.markdown MarkdownProcessor])
	(:import [java.io File]))

; keep the options as a ref available
(def options (ref []))

(defn get-toc-file[]
	(str (@options :output) "/" (@options :toc-filename)))

(defn write
	"Helper method. Writes/Append to file"
	([text dontappend] (spit (get-toc-file) text))
	([text] (spit (get-toc-file) text :append true)))

(defn mp
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

(defn rd 
	"Parse a file, turn it to HTML and spit the clean content to a file"
	[filepath]
	(let [
		md (mp filepath)
		parsed (parse md)
		anchored (anchors (.clone parsed))
		clean  (select "body > *" anchored)
	]
	(spit (str (@options :output) "/" (.getName (io/file filepath)) ".html") clean)
	parsed))

(defn tic
	"Remove all the tags except header tags to keep doc structure"
	[content]
	(doseq [t ["p" "ul" "li" "a" "img" "pre" "code" "blockquote" ]]
		(.remove (select t content)))
	content)

(defn toc_all
	"Prepare a TOC from all the files, recursively"
	[base]
	(doseq [md (glob (str base "/**/*.md"))] 
		(let [ 
			md-content-no-anchors (rd md)
			cleaned (tic md-content-no-anchors)]
		  (write ($ cleaned "body > *")))))

(defn toc
	"Main method"
	[base] 
	; clean up previous file
	(write "" false)
	(if (@options :customization)
	 (write (slurp (str (@options :customization) "/header.html"))))
	(toc_all base)
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