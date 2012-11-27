(ns medic.pre
	(:import java.io.File)
	(:use clojure.java.io))

; try to make it multiline 
(def pre-process-pattern #"@@@ (.*) (.*) @@@")
(def toc-int (ref 6))

; for compatibility
(defn ruby[file args]
	(code file args))

(defn toc-i[] @toc-int)

(defn toc
	"Create a place holder for the doc"
	[file args]
	(dosync (ref-set toc-int (Integer/parseInt args)))
	"<div id=\"toc\"></div>")

(defn code[file args]
	(str "<pre>" (slurp args) "</pre>"))

(defn exec[file args]
	(load-string args))

(defn include[file filename]
	(slurp (str 
		(.getParent (as-file (.getAbsolutePath (as-file file)))) 
		"/" 
		filename)))

(defn pre-process[file line]
	(let[[l method args] (re-find pre-process-pattern line)] 
			(if method
			  (eval (list (symbol (str "medic.pre/" method)) file args))
			  line)))

(defn pre-read[file]
	(let[tmp-file (File/createTempFile "tmp" "")]
		(doseq [line (line-seq (reader file))]
			(spit tmp-file (pre-process file line) :append true)
			(spit tmp-file "\n" :append true))
		(slurp tmp-file)))