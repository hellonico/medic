(ns medic.pre
	(:use clojure.java.io))

(def pre-process-pattern #"@@@ (.*) (.*) @@@")

(defn pre-process[file line]
	(let[[l method args] (re-find pre-process-pattern line)] 
			(if method
			  (eval (list (symbol method) file args))
			  line)))

(defn pre-read[file]
	(doseq [line (line-seq (reader file))]
		(println (pre-process file line))))

(defn include[file path]
	(slurp path))

(defn local[file filename]
	(slurp (str 
		(.getParent (as-file (.getAbsolutePath (as-file file)))) 
		"/" 
		filename)))

; (pre-read "text/includes.md")
; (doseq [m (.getMethods (.getClass (as-file "project.clj")))] (println m))