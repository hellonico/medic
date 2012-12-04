(use 'medic.core) 
(medic.core/toc-with-options 
	{:folder "doc" 
	 :customization "doc/html"  
	 :output "output" 
	 :one true 
	 :clean true 
	 :embed true
	})