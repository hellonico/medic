(ns medic.light
	(:use jsoup.soup)
	(:import [com.threecrickets.jygments.grammar Lexer])
	(:import [java.io File FileWriter])
	(:import [com.threecrickets.jygments.format Formatter]))

(defn light-file
	"Turn code into html"
	([in-file] (light-file in-file (FileWriter. (str in-file ".html"))))
	([in-file out-file]
		(.format (Formatter/getByName "html") 
			(.getTokens (Lexer/getForFileName in-file) (slurp in-file))
			out-file)))

(defn lightify
	[in-file]
	(let [tmp-file (File/createTempFile "tmp" "") ]
		(light-file in-file (FileWriter. tmp-file))
		(str (select "body > div" (parse (slurp tmp-file))))))