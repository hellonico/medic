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
	[html-file htag]
	(let [
		filename (.getName (io/file html-file)) 
		link (str filename "#" (sanitize (.html htag)))
		]
	  (.html htag (str 
	  	"<a target=\"_blank\" href=\"" 
	  	link 
	  	"\">" 
	  	(.html htag) 
	  	"</a>"))))

(defn linkify-html
	"Add a link to all header tags of an html file"
	[html-file content]
	(doseq [htag htags]
		(doseq [h (select htag content)]
			(linkify-tag html-file h)))
	content)

(defn html-with-anchors 
	"Puts anchors in the HTML and clean it"
	[parsed]
	(select "body > *" (anchorify-html (.clone parsed))))