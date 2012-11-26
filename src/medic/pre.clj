(ns medic.pre
	(:use clojure.java.io))

(def pre-process-pattern #"@@@ (.*) (.*) @@@")

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
	(doseq [line (line-seq (reader file))]
		(println (pre-process file line))))