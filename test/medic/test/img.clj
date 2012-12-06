(ns medic.test.img
	(:use clojure.test)
	(:use medic.parse))

(defn test-conversion
	[file expected]
	(let [converted (markup-file-to-html file)]
	(doseq [ex expected]
		(is (.contains converted ex)))))
	
(deftest images
	(test-conversion "text/img.md" ["<img"]))

(deftest tables
	(test-conversion "text/table.md" ["<table"]))
