(ns medic.pre
	(:use medic.light)
	(:use clojure.java.io)
	(:import java.io.File))

; in the future try to make it multiline 
(def pre-process-pattern #"@@@ (.*) (.*) @@@")
(def toc-int (ref 6))
(defn toc-i[] @toc-int)

(defn toc
	"Create a place holder in the html file for the toc"
	[file args]
	(dosync (ref-set toc-int (Integer/parseInt args)))
	"<div id=\"toc\"></div>")

(defn exec
	"Execute a line of code and return the result"
	[file args]
	  (load-string args))

(defn- path-from-same-folder
	[file filename]
	(str 
		(.getParent (as-file (.getAbsolutePath (as-file file)))) 
		"/" 
		filename))

(defn include
	"include a file relative to the given file"
	[file filename]
	(slurp (path-from-same-folder file filename)))

(defn code
	"Block code"
	[file args]
	  (lightify (path-from-same-folder file args)))

; for compatibility with kitabu, but broken
(defn ruby
	[file args]
	(str "<pre>" "ruby " file "</pre>"))

(defn pre-process
	"Pre process line of file"
	[file line]
	(let[[l method args] (re-find pre-process-pattern line)] 
			(if method
				 (try 
				  (eval (list (symbol (str "medic.pre/" method)) file args))
				  (catch Exception e (println "pre-process catch [" method "]:" e))
				  (finally line))
			  line)))

(defn pre-process-file
	"Pre process a whole file"
	[file]
	(let[tmp-file (File/createTempFile "tmp" "") 
		 ; make sure this is a path
		 filepath (.getPath (as-file file))]
		(doseq [line (line-seq (reader file))]
			(spit tmp-file (pre-process filepath line) :append true)
			(spit tmp-file "\n" :append true))
		(slurp tmp-file)))
