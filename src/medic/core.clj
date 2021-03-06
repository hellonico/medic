(ns medic.core
	(:gen-class :main true)
	(:use medic.modify)
	(:use medic.parse)
	(:use medic.default)
	(:use medic.pdf)
	(:use medic.kusuri)
	(:use org.satta.glob)
	(:use jsoup.soup)
	(:use [clojure.tools.cli :only [cli]])
	(:require [clojure.java.io :as io])
	(:import [java.io File]))

; keep the options as a ref available
; set defaults here
(def defaults {   
	    :customization "doc" 
		:one true 
		:clean false 
		:embed true
		:folder "doc" 
		:output "output" 
		:toc-filename "toc.html"})
(def options (ref defaults))

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

(defn write-toc
	"Helper method. write-tocs/Append to file"
	([text dontappend] (spit (path-to-toc) text))
	([text] (spit (path-to-toc) text :append true)))

(defn toc-one
	"Remove all the tags except header tags to keep doc structure
	Warning! Content is modified now."
	[content]
	(doseq [t ["table" "p" "ul" "li" "a" "img" "pre" "code" "blockquote" ]]
		(.remove (select t content)))
	($ content "body > *"))


(defn wrap-if-needed
	"Wrap the given html file with a header and a footer"
	[html-file ext]
	(if (@options :customization)
		(try 
			(do 
			(concat-files 
				[
				(str (@options :customization) "/html/header-" ext ".html")
				html-file
				(str (@options :customization) "/html/footer-" ext ".html")
				]
				(str html-file ".tmp")
				)
			(io/delete-file html-file)
			(.renameTo 
				(io/as-file (str html-file ".tmp")) 
				(io/as-file html-file)))
			(catch Exception e (println "In customize catch. Please add necessary files." e)))))

(defn process-content
	"Process the content of a markup file"
	[markup-file]
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
 	 ; do not wrap each time when generating one file
 	 (if (not (@options :one))
 	 	(wrap-if-needed html-output-file "single"))
 	 ; write-toc toc to file
     (write-toc 
     	(linkify-html html-output-file (toc-one parsed) (@options :one)))))

(defn clean-up
	"clean up previous files: TOC and one file html"
	[]
	(if (@options :clean) 
		(try 
			(delete-file-recursively (io/as-file (@options :output)) true)
			(catch Exception e (println e))))
	; make sure we have the output directory
	(.mkdir (io/as-file (@options :output)))
	(if (@options :one) (spit (path-to-html-output) "")))

(defn insert-toc
	"Concat the toc and the one file into a single html file"
	[]
	(let [
		toc-file (path-to-toc)
		one-file (path-to-html-output)
		tmp-file (str (@options :output) "/two.html")]
		(concat-files [toc-file one-file] tmp-file)
		(.delete (io/as-file one-file))
		(.renameTo (io/as-file tmp-file) (io/as-file one-file))
		; TOC can not be used as is, so delete it
		(.delete (io/as-file toc-file))
		))

(defn toc-files
	"Process all <files>"
	[files]
		(clean-up)	

		; start toc
		(write-toc "<div id=\"toc\">" false)

		(doseq [markup-file files] 
		  (process-content markup-file))

		; finish toc
		(write-toc "</div>")

		; 
		(if (not (@options :one))
			(wrap-if-needed (path-to-toc) "toc"))

		; test all in one page
		(if (@options :one)
			(do
				(if (@options :embed) 
					(insert-toc))
				(wrap-if-needed (path-to-html-output) "one")
				(generate-pdf 
					[(path-to-html-output)] 
					(str (path-to-html-output) ".pdf")
					(str (@options :customization) "/fonts")))))

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
	(dosync (ref-set options (merge defaults -options))))

(defn toc-with-options
	"Use the given options to generate the content"
	[loptions]
		(set-options loptions)
		(println "Using parameters:" @options)
		(toc (@options :folder)))

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
     			["-e" "--embed" 
     				"Embed TOC in output" :flag true]	
     			; ["--pdf"
     			; 	"Generate PDF" :default false]	
     			["-1" "--one"  
     				"one html file for all the markdown output." :flag true]
     			["-x" "--clean" 
     				"Delete all files in output dir before running." :flag true]	
     			["-d" "--folder" 
     				"The top folder containing the markdown files" :default "text"]
     			["-c" "--customization" 
     				"An assets folder. See documentation for more info"])]
	(if (contains? loptions :help)
		(println banner)
		(toc-with-options loptions))		
	(System/exit 0)))