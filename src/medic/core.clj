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
; one for all
; (def bit-mode (bit-or org.pegdown.Extensions/ALL org.pegdown.Extensions/))
; (def bit-mode 0)
(def peg (org.pegdown.PegDownProcessor.))

(defn path-to-toc[]
	(str (@options :output) "/" (@options :toc-filename)))

(defn write
	"Helper method. Writes/Append to file"
	([text dontappend] (spit (path-to-toc) text))
	([text] (spit (path-to-toc) text :append true)))

(defn markup-to-html
	[content]
	(.markdownToHtml peg content))


(defn markup-file-to-html
	"turn markup into html"
	[filepath]
		(markup-to-html (slurp filepath)))

; (.markdown (MarkdownProcessor.) (slurp filepath)))

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
	(if (@options :one)
		(str 
			(@options :output) "/one.html" )
		(str 
			(@options :output) 
			"/" 
			(.getName (io/file filepath)) 
			".html")))

(defn parse-file
	"pre process and parse markdown to html"
	[filepath]
		(parse (markup-to-html (pre/pre-process-file filepath))))

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
 	 	(html-with-anchors parsed) :append (@options :one))
 	 ; write toc to file
     (write 
     	(linkify-toc html-output-file (toc-one parsed)))))

(defn toc-files
	"Process all <files>"
	[files]
		; clean up previous files
		(write "" false)
		(if (@options :one)
			(spit (path-to-html-output nil) ""))
		(if (@options :customization)
	 		(write (slurp (str (@options :customization) "/header.html"))))
		(doseq [markup-file files] 
		  (process-content markup-file))
		(if (@options :customization)
	 		(write (slurp (str (@options :customization) "/footer.html")))))

(defn toc-folder
	"Prepare a TOC from files found in a folder"
	[base]
		(toc-files (glob (str base file-regexp))))

(defn toc
	"Main method"
	[base] 
		(toc-folder base))


(defn -main
	"Main method. Will be called from the command line"
	[& args]
	(let [
		[loptions args banner]  
			(cli args
				["-h" "--help" "Print this message"]
     			["-o" "--output" "Output folder" :default "output"] 
     			["-toc" "--toc-filename" "TOC filename" :default "toc.html"]
     			["--one" "one html file for all the markdown output" :default false]
     			["-d" "--folder" "The top folder with the markdown files" :default "text"]
     			["-c" "--customization" "A folder with header.html, footer.html"])]

	(if (contains? loptions :help)
		(println banner)
		(do
			(dosync (ref-set options loptions))
			; make sure we have the output directory
			(.mkdir (io/as-file (@options :output)))
			(println "Using parameters:" @options)
			(toc (@options :folder))))
	(System/exit 0)))

(dosync 
	(ref-set options 
		{
		 :customization "public/html"
		 :folder "../niclojure/textja"
		 :toc-filename "toc.html"
		 :one false
		 :output "output"}))
