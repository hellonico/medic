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
(def options (ref {}))
; keep those tags here
(def htags (map #(str "h" %) (range 1 7)))
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

(defn sanitize
	[html]
	(.replaceAll html " " ""))

(defn anchorify
	"wrap a tag with a name anchor"
	[htag]
	(let[sanity (sanitize (.html htag))]
	(.html htag 
		(str 
		 "<" (.tagName htag) ">" 
		 (.html htag) 
		 "</" (.tagName htag) ">" 
		 "</a>"))
	(.tagName htag "a")
	(.attr htag "name" sanity)))

; (def h1 (.clone (first (select "h1" p1))))

(defn linkify-tag
	"Change a header tag to the same header with a link inside"
	[html-file htag]
	(let [
		filename (.getName (io/file html-file)) 
		link (str filename "#" (sanitize (.html htag)))
		]
	  (.html htag (str 
	  	"<a target=\"_blank\" href=\"" 
	  	link 
	  	"\">" 
	  	(.html htag) 
	  	"</a>"))))

(defn linkify-toc
	[html-file content]
	(doseq [htag htags]
		(doseq [h (select htag content)]
			(linkify-tag html-file h)))
	content)

(defn anchors
	"wrap all header tags with name anchors"
	[content]
	(doseq [htag htags]
		(doseq [h (select htag content)]
			(anchorify h)))
	content)

(defn html-with-anchors 
	"Puts anchors in the HTML and clean it"
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
	"Remove all the tags except header tags to keep doc structure
	Warning! Content is modified now."
	[content]
	(doseq [t ["p" "ul" "li" "a" "img" "pre" "code" "blockquote" ]]
		(.remove (select t content)))
	($ content "body > *"))

(defn process-content
	"Process the content of a markup file"
	[markup-file]
	(let [ 
		parsed (parse-file markup-file)
		html-output-file (path-to-html-output markup-file) 
	 ] 
	 ; write html to file
 	 (spit 
 	 	html-output-file
 	 	(html-with-anchors parsed))
 	 ; write toc to file
     (write 
     	(linkify-toc html-output-file (toc-one parsed)))))

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

(dosync 
	(ref-set options 
		{:folder "../niclojure/texten"
		 :toc-filename "toc.html" 
		 :output "output"}))

(def t1 (toc-one (parse-file "text/simple.md")))
(println (linkify-toc "a.html" t1))