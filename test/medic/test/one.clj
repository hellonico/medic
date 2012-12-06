(ns medic.test.one
  (:require medic.pdf)
  (:use midje.sweet)
  (:use [clojure.java.io :as io])
  (:use medic.core)
  (:use medic.modify)
  (:use medic.parse)
  (:use clojure.test)
  (:use jsoup.soup))

(deftest one-file
	(set-options 
		{
		 :customization "public"
		 :folder "../niclojure/textja"
		 :toc-filename "toc.html"
		 :one true
		 :output "output"})
	(toc-folder "../niclojure/textja")
	(is (.exists (io/as-file "output/one.html")) "One file does not exist"))
