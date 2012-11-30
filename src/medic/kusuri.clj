(ns medic.kusuri
	(:use clojure.java.io))

(defn concat-files
	"Concat all files in another file"
	[files out-file]
	(with-open [os (output-stream out-file)]
		(doseq [file files]
			(copy (as-file file) os))))