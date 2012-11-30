(ns medic.test.light
	(:use org.satta.glob)
	(:use clojure.test)
	(:use clojure.java.io)
	(:use medic.light))

(deftest colors
	(doseq [file (glob "text/test.*")]
		(light-file (.getPath (as-file file)))))