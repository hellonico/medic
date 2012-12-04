(ns medic.pdf
  (:use clojure.java.io)
  (:use org.satta.glob)
  (:import [java.io FileOutputStream])
  (:import [org.xhtmlrenderer.pdf ITextRenderer]))

(defn- get-factory
  "Get me an old fashion sax parser factory that doesn't screw up"
  []
    (doto (javax.xml.parsers.DocumentBuilderFactory/newInstance)
      (.setValidating false)
      (.setFeature "http://apache.org/xml/features/nonvalidating/load-dtd-grammar" false)
      (.setFeature "http://apache.org/xml/features/nonvalidating/load-external-dtd" false)
      (.setFeature "http://xml.org/sax/features/validation" false)))
(def factory (get-factory))

(defn- get-doc
  "This parse the document with the proper sax properties"
  [filepath]
    (.parse (.newDocumentBuilder factory) (as-file filepath)))

(defn- get-parent
  "Needed so that CSS files are resolved properly"
  [filepath]
  (-> 
    (as-file filepath)
    (.getParentFile)
    (.toURI)
    (.toURL)
    (.toExternalForm)))

(defn- add-file-to-pdf
  "Cannot use this on its own. This is called by generate-pdf to add more files."
  [renderer filepath]
     (doto renderer
      (.setDocument (get-doc filepath) (get-parent filepath))
      (.layout)
      (.writeNextDocument)))

(defn- add-fonts
    "Add fonts to the pdf renderer"
    [renderer font-folder]
    (let 
      [resolver (.getFontResolver renderer)]
      (doseq [font (glob (str font-folder "/*"))]
        (println "Adding font" font)
        (.addFont resolver (.getAbsolutePath font) com.lowagie.text.pdf.BaseFont/IDENTITY_H true))))

(defn generate-pdf
   "Collect a set of hmtl files and stick them together in a pdf file."
   ([in-files out-file] (generate-pdf in-files out-file nil))
   ([in-files out-file font-folder]
    (let [renderer (ITextRenderer.) first-file (first in-files)]
     ; add fonts if possible
     (if (and font-folder (.exists (as-file font-folder)))
        (add-fonts renderer font-folder))
     (with-open [os (output-stream out-file)]
      ; the first file is handled slightly differently by flying saucer
      ; it handles the style and the layout
      (doto renderer 
        (.setDocument (get-doc first-file) (get-parent first-file))
        (.layout)
        (.createPDF os false))
      (doseq [file (remove #{first-file} in-files)]
        (add-file-to-pdf renderer file))
      (.finishPDF renderer)))))
