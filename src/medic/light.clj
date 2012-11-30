(ns medic.light
	(:import [com.threecrickets.jygments.grammar Lexer])
	(:import [java.io FileWriter])
	(:import [com.threecrickets.jygments.format Formatter]))

(defn light-file
	"Turn code into html"
	([in-file] (light-file in-file (FileWriter. (str in-file ".html"))))
	([in-file out-file]
		(.format (Formatter/getByName "html") 
			(.getTokens (Lexer/getForFileName in-file) (slurp in-file))
			out-file)))