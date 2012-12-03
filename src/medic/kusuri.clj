(ns medic.kusuri
	(:use clojure.java.io))

(defn concat-files
	"Concat all files in another file"
	[files out-file]
	(with-open [os (output-stream out-file)]
		(doseq [file files]
			(copy (as-file file) os))))

; copied from old contrib.io
(defn delete-file-recursively
  "Delete file f. If it's a directory, recursively delete all its contents.
Raise an exception if any deletion fails unless silently is true."
  [f & [silently]]
  (let [f (file f)]
    (if (.isDirectory f)
      (doseq [child (.listFiles f)]
        (delete-file-recursively child silently)))
    (delete-file f silently)))