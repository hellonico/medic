(ns medic.test.light
	(:use org.satta.glob)
	(:use clojure.test)
	(:use clojure.java.io)
	(:use medic.light))

; clean up
(doseq [f (glob "text/*.html")] (.delete f))

(deftest colors
	(doseq [file (glob "text/test.*")]
		(light-file (.getPath (as-file file)))))

(deftest one-color
	(let [
		there-is-light (lightify "text/test.clj")
		]
	; (println there-is-light)
	(is (not (.contains there-is-light "<body")) "Code invalid")
	(is (.contains there-is-light "getMethods") "Code invalid")))