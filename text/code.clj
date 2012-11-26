(println "this is a test")

(doseq [m (.getMethods (.getClass (as-file "project.clj")))] 
	(println m))