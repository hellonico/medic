<!-- Start With a picture --> 

![medic](../doc/medic.png)

# Features

* Convert a set of markdown files to HTML
* Support for subfolders 
* Generate a TOC from all the headers tag in the documents
* Make the TOC linkable and embeddable or separated. 
* Generate HTML for all files into a unique file or separately
* Insert code text and other expressions into the HTML
* Export result to one PDF file
* Colorize imported code

# Quickstart 

The basic way to run the tool is a regular java command line:

      java -jar medic.jar <options>
      
The list of options can be displayed with

      java -jar medic.jar -h
      
And should output something similar to:

<pre><code>
@@@ include help.sh @@@
</pre></code>

## Options

| Option Name | Long name | Sample Value | Usage |
| --- | --- | --- | --- | 
| -h | --help | _flag_ | Output the help banner | 
| -d | --folder | doc | Specify the input directory, where to find the markdown files | 
| -x | --clean | _flag_ | Flag to tell engine to clean the output directory before running | 
| -o | --output | output | Path to the output directory | 
| -c | --customization | doc | Path where to find headers and footers file to wrap the different outputs. Customization for html files will be in a subfolder named html, and fonts in a fonts subfolder. | 
| -e | --embed | _flag_ | Should the TOC be embedded in the generated file. 
| -1 | --one | _flag_ | Aggregate all the generated html files into a single one.html file | 

# Preprocessors

## Overview

The tool has a number of preprocessors so you can include some custom processing. The current list is:

| Preprocessor | Arguments | Usage |
| --- | --- | --- | 
| code | _path_ | Include the code from specified path and highlight it with colors | 
| include | _path_ | include the file specified by path. If it is a markdown file it will be turn to html like the rest | 
| exec | _exp_ | execute the embeded clojure expression ;) and inserts the result into the file | 

## Usage 

Processors can be inserted into the markdown file, using the @@@ symbol before and after on the same line, like this:
    
![code](../doc/mark.png)

# About Pegdown

[Pegdown](https://github.com/sirthias/pegdown) is a markdown to html converter. It supports the core markdown syntax plus quite a few other extensions, notably those from [Multimarkdown](http://fletcherpenney.net/multimarkdown/).

The project examples can be found on [github](https://github.com/sirthias/pegdown/tree/master/src/test/resources) so have a look if you get stuck. 

# Cookbooks

Here a few one liners to generate the documentation you want. 

## One html file and one pdf file from markdown files. Embed the TOC

    java -jar medic.jar -d doc -x -o output -c doc -e -1
    
Note that the _-x_ flag will also empty the output folder before generating new files

## Generate one file for the TOC and parse all files to html separately

    java -jar medic.jar -d doc -x -o output -c doc
    
## Generate one file for the TOC and parse all files to html into one file

    java -jar medic.jar -d doc -x -o output -c doc -1
    
# Re-using the JVM with Jark

Jark is a useful tool for reusing a running the Java Virtual Machine between runs. 

## Getting The encironment ready.

* First you need to clone this repository completely
* Then install a tool called [lein](https://github.com/technomancy/leiningen), which is a essentially a build tool
* use *lein deps* to collect all the libraries needed to run this project
* Then download the JVM runner binary from [here](http://icylisper.github.com/jark/downloads.html) and add it to your path.
* Finally, start jark with: 

> scripts/jark_ctl start*

Voila. 

## Create this documentation

Use the script named 

> scripts/generate-doc-with-jark.sh*

It will do the same as *scripts/generate-doc.sh* but delegating the execution to the jark managed Virtual Machine, thus making it really fast.


# Customization: CSS, fonts and html wrappers

## Basics

The customization folder, is supposed to have the given structure:

> <customization_folder>
>    /html 
>    /fonts

## Fonts

Any file in the fonts folder, will be added to the generated pdf. You can also add them using the usual CSS3 font-face syntax.

> @font-face {
>    font-family: DeliciousRoman;
>    src: url(../fonts/Walkway_SemiBold.ttf);
>    font-weight:400;
> }

## CSS 

Flying Saucer has [documentation](http://flyingsaucerproject.github.com/flyingsaucer/r8/guide/users-guide-R8.html#xil_43) about special CSS styles to put in when generating PDFs.

### Note on Page Breaks

Remember, You can also make use of [page-breaks](http://davidwalsh.name/css-page-breaks) and other print specific directives.

## HTML

There is a set of header and footer in HTML for different scenario.

| Names | Description |
| --- | --- | 
| header-one.html, footer-one.html  | Used when the flag one is set, which means outputting for a single page | 
| header-toc.html, footer-toc.html | Used to wrap the TOC only, when generated as a separate file. flag one is not set |
| header-single.html, footer-single.html | Used when flag one is not set, on each converted markdown page. |

# Testing

If you have the source code, you can use the script:

> ./scripts/test-watch.sh

It will execute all the tests, and wait for your any changes in the code to re-run them.
If you just want to run tests once. Use:

> lein test