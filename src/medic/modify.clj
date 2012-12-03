(ns medic.modify
	(:require [clojure.java.io :as io])
	(:use jsoup.soup)
	(:use medic.default))

(defn sanitize
	"Simple sanitizing method for creating links"
	[html]
	  (.replaceAll html " " ""))

(defn anchorify
	"wrap a tag with a name anchor"
	[htag]
	(let[sanity (sanitize (.html htag))]
	(.html htag 
		(str 
		 "<" (.tagName htag) ">" 
		 (.html htag) 
		 "</" (.tagName htag) ">" 
		 "</a>"))
	(.tagName htag "a")
	(.attr htag "name" sanity)))

(defn anchorify-html
	"wrap all header tags with name anchors"
	[content]
	(doseq [htag htags]
		(doseq [h (select htag content)]
			(anchorify h)))
	content)

(defn linkify-tag
	"Change a header tag to the same header with a link inside"
	[html-file htag standalone]
	(let [
		filename (.getName (io/file html-file)) 
		link-no-file (str "#" (sanitize (.html htag)))
		link-with-file (str filename link-no-file)
		href (if standalone
			  (str "href=\"" link-no-file)
			  (str "target=\"one\" href=\"" link-with-file))
		]
	  (.html htag (str "<a " href "\">" (.html htag) "</a>"))))

(defn linkify-html
	"Add a link to all header tags of an html file"
	[html-file content standalone]
	(doseq [htag htags]
		(doseq [h (select htag content)]
			(linkify-tag html-file h standalone)))
	content)

(defn html-with-anchors 
	"Puts anchors in the HTML and clean it"
	[parsed]
	(select "body > *" (anchorify-html (.clone parsed))))