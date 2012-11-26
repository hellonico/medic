(ns mdtoc.test.core
  ; (:use [midje.checkers])
  (:use [midje.sweet])
  (:use [mdtoc.core])
  (:use jsoup.soup))

; (deftest replace-me ;; FIXME: write
;   (is false "No tests have been written."))

(deftest anchorfy-a-tag
  (let
  	[
  	m1 (mp "text/this_is_section_1.md")
  	p1 (parse m1)
  	hh (first (select "h1" p1))
  	]
   (is false "Need work")))

; (deftest generate-separate-toc-from-files
;   (is false "Need work"))

(fact "Anchor a tag"
   (anchorfy hh) => "<h1></h1>")