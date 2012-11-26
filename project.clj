(defproject medic "1.0.0"
  :description "Create a TOC from a set of markdown files"
  :main medic.core
  ; :dev-dependencies [
  ; 	[midje "1.4.0"]
  ; ]
  :dependencies [
    [com.uwyn/jhighlight "1.0"]
  	[org.clojure/clojure "1.5.0-beta1"]
  	[org.clojure/tools.cli "0.2.2"]
  	[org.clojure/data.json "0.1.3"]
  	[org.clojars.hozumi/clj-glob "0.1.2"]
	  [clojure-soup/clojure-soup "0.0.1"]
	  [org.markdownj/markdownj "0.3.0-1.0.2b4"]
   ])