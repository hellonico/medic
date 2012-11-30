(ns medic.test.core
  (:require medic.pdf)
  (:use midje.sweet)
  (:use org.satta.glob)
  (:use [clojure.java.io :as io])
  (:use medic.core)
  (:use medic.modify)
  (:use medic.parse)
  (:use medic.kusuri)
  (:use clojure.test)
  (:use jsoup.soup))

; clean up
(doseq [f (glob "text/*.html")] (.delete f))

(set-options 
		{
		 :customization "public/html"
		 :folder "../niclojure/textja"
		 :toc-filename "toc.html"
		 :one false
		 :output "output"})

(deftest concat-me
	(concat-files (glob "text/*.txt") "output/concat.txt"))

(deftest sanitize-me
	(is (= (sanitize "1 2 3") "123")))

(deftest write-me
	(write-toc "hello" false)
	(is (= (slurp (path-to-toc)) "hello") "hello")
	(write-toc "hello")
	(is (= (slurp (path-to-toc)) "hellohello") "hello")
	(is (.delete (as-file (path-to-toc))) "cannot delete toc file"))

(def m1 (markup-file-to-html "text/this_is_section_1.md"))
(def p1 (parse m1))

(deftest parse-me
  (is (not (nil? m1)) "Markup failed.")
  (is (not (empty? m1)) "Parsing succeeded but empty.")
  (is (not (nil? p1)) "Parsing failed."))

(def hh (first (select "h1" p1)))

(deftest anchorify-me
	(anchorify hh)
	(is (=
		(str hh)
		"<a name=\"DocumentationNotes\"><h1>Documentation Notes</h1></a>")
		"Cannot anchor a tag"))

(deftest path-to-doc-me
	(is (= "output/toc.html" (path-to-toc)) "Test default path to doc"))

(deftest html-me
	(toc-files ["text/this_is_section_1.md"])
	(is (.exists (io/as-file "output/toc.html")) "TOC does not exist"))

(deftest pdf-me
	(medic.pdf/generate-pdf ["output/toc.html"] "output/toc.pdf")
	(is (.exists (io/as-file "output/toc.pdf")) "TOC does not exist"))

(deftest big-toc
	(toc-folder "../niclojure/textja")
	(medic.pdf/generate-pdf ["output/toc.html"] "output/toc2.pdf"))

(deftest include-me
	(toc-files ["text/includes.md"])
	(is (not (.contains (slurp "output/includes.md.html") "@@@")) "Pre processing failed"))

(deftest all-md
	(toc-regexp "text/inc*.md"))
