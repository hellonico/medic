(ns medic.core
	(:gen-class :main true)
	(:use medic.modify)
	(:use medic.parse)
	(:use medic.default)
	(:use org.satta.glob)
	(:use jsoup.soup)
	(:use [clojure.tools.cli :only [cli]])
	(:require [clojure.java.io :as io])
	(:import [java.io File]))

; keep the options as a ref available
(def options (ref {}))
; should be as an option
(def file-regexp "/**/*.md")

(defn path-to-toc[]
	(str (@options :output) "/" (@options :toc-filename)))

(defn path-to-html-output
	"Find the path to output html version of a markup file"
	([] (str (@options :output) "/one.html" ))
	([filepath]
		(str 
			(@options :output) 
			"/" 
			(.getName (io/file filepath)) 
			".html")))

(defn write
	"Helper method. Writes/Append to file"
	([text dontappend] (spit (path-to-toc) text))
	([text] (spit (path-to-toc) text :append true)))

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
	; (println "Processing:" markup-file)
	(let [ 
		parsed (parse-file markup-file)
		html-output-file 
			(if (@options :one)
				(path-to-html-output) 
				(path-to-html-output markup-file))
	 ] 
	 ; write html to file
 	 (spit 
 	 	html-output-file
 	 	(html-with-anchors parsed) :append (@options :one))
 	 ; write toc to file
     (write 
     	(linkify-html html-output-file (toc-one parsed)))))

(defn toc-files
	"Process all <files>"
	[files]
		; clean up previous files
		(write "" false)
		(if (@options :one)
			(spit (path-to-html-output) ""))
		(if (@options :customization)
	 		(write (slurp (str (@options :customization) "/header.html"))))
		(doseq [markup-file files] 
		  (process-content markup-file))
		(if (@options :customization)
	 		(write (slurp (str (@options :customization) "/footer.html")))))

(defn toc-regexp
	"Convenience method for easy globing of files"
	[regexp]
	(toc-files (glob regexp)))

(defn toc-folder
	"Prepare a TOC from files found in a folder"
	[base]
	(toc-regexp (str base file-regexp)))

(defn toc
	"Main method"
	[base] 
		(toc-folder base))

(defn set-options
	[-options]
	(dosync (ref-set options -options)))

(defn -main
	"Main method. Will be called from the command line"
	[& args]
	(let [
		[loptions args banner]  
			(cli args
				["-h" "--help" 
					"Print this message"]
     			["-o" "--output" 
     				"Output folder" :default "output"] 
     			["-toc" "--toc-filename" 
     				"TOC filename" :default "toc.html"]
     			["--one" 
     				"one html file for all the markdown output." :default false]
     			["-d" "--folder" 
     				"The top folder containing the markdown files" :default "text"]
     			["-c" "--customization" 
     				"A folder with header.html, footer.html"])]

	(if (contains? loptions :help)
		(println banner)
		(do
			(set-options loptions)
			; make sure we have the output directory
			(.mkdir (io/as-file (@options :output)))
			(println "Using parameters:" @options)
			(toc (@options :folder))))
	(System/exit 0)))
