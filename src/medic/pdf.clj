(ns medic.pdf
  (:use clojure.java.io)
  (:import [java.io FileOutputStream])
  (:import [org.xhtmlrenderer.pdf ITextRenderer]))

(defn add-file-to-pdf
  [renderer document]
     (doto renderer
      (.setDocument document)
      (.layout)
      (.writeNextDocument)))

(defn generate-pdf [in-files out-file]
   (let [renderer (ITextRenderer.) first-file (first in-files)]
     (with-open [os (output-stream out-file)]

      ; the first file is handled slightly differently by flying saucer
      ; it handles the style and the layout
      (doto renderer 
        (.setDocument (as-file first-file))
        (.layout)
        (.createPDF os false))
      (doseq [file (remove #{first-file} in-files)]
        (add-file-to-pdf renderer (as-file file)))
      (.finishPDF renderer))))