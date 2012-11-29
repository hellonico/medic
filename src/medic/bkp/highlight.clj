
(defn test-highlight[]
	(import 'syntaxhighlighter.brush.BrushJScript)
	(import 'syntaxhighlighter.SyntaxHighlighterParser)
	(import 'syntaxhighlight.SyntaxHighlighter)
	(import 'syntaxhighlighter.theme.ThemeRDark)

	(def parser (SyntaxHighlighterParser. (BrushJScript.)))
	(.setHtmlScript parser true)
	(.setHTMLScriptBrushes parser [ (BrushJScript.)])
	
	(def highlighter  (SyntaxHighlighter. parser (ThemeRDark.)))
	(.setContent highlighter (as-file "text/test.js"))

	(def frame (javax.swing.JFrame.))
	(.setContentPane frame highlighter)
	(.pack frame)
	(.setVisible frame true)
	)