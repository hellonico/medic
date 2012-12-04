(ns medic.parse
	(:require [medic.pre :as pre])
	(:use jsoup.soup)
	(:import [org.pegdown PegDownProcessor]))

; one processor for all runs
(def peg (PegDownProcessor.  org.pegdown.Extensions/ALL))

(defn markup-to-html
	"Convert some markup file as string to html"
	[content]
	(.markdownToHtml peg content))

(defn markup-file-to-html
	"turn markup into html"
	[filepath]
		(markup-to-html (slurp filepath)))

(defn parse-file
	"pre process and parse markdown to html"
	[filepath]
		(parse (markup-to-html (pre/pre-process-file filepath))))
