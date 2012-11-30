(ns medic.test.img
	(:use clojure.test)
	(:use medic.parse))

(deftest images
	(is (.contains (markup-file-to-html "text/img.md") "<img")))
