(defproject medic "1.0.0"
  :description "TOCify, Includify, PDFfy a set of markdown files"
  :main medic.core
  :dev-dependencies [
  	[midje "1.4.0"]
    [com.stuartsierra/lazytest "1.2.3"]
    [lein-test-out "0.1.0"]
  ]
  :repositories {
    ; lazytest
    "stuart" "http://stuartsierra.com/maven2"
    ; jygments
    "crickets" "http://repository.threecrickets.com/maven/"
  }
  :dependencies [
  	[org.clojure/clojure "1.5.0-beta1"]
  	[org.clojure/tools.cli "0.2.2"]
  	[org.clojure/data.json "0.1.3"]
  	[org.clojars.hozumi/clj-glob "0.1.2"]
	  [clojure-soup/clojure-soup "0.0.1"]
    [com.threecrickets.jygments/jygments "0.9.0"]
    [org.pegdown/pegdown "1.2.0"]
    [org.parboiled/parboiled-java "1.1.3"]

    ; not needed ? ? ?
    ; [org.parboiled/parboiled-core "1.1.3"]
    ; [asm/asm-all "3.3.1"]

    ; pdf generation
    [com.lowagie/itext "2.0.8"
    :exclusions [
     bctsp/bcmail-jdk14 
     org.apache.ant/ant 
     org.apache.ant/ant-launcher]]
    [de.huxhorn.lilith/de.huxhorn.lilith.3rdparty.flyingsaucer.core-renderer "8RC1" 
    :exclusions [
     bctsp/bcmail-jdk14 
     org.apache.ant/ant 
     org.apache.ant/ant-launcher]]    
   ])