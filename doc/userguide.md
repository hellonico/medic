<!-- Start With a picture --> 

![medic](../doc/medic.jpg)

# Features

* Convert a set of markdown files to HTML
* Support for subfolders 
* Generate a TOC from all the headers tag in the documents
* Make the TOC linkable and embeddable or separated. 
* Generate HTML for all files into a unique file or separately
* Insert code text and other expressions into the HTML
* Export result to one PDF file
* Colorize imported code

## Quickstart 

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
| -c | --customization | doc/html | Path where to find headers and footers file to wrap the different outputs | 
| -e | --embed | _flag_ | Should the TOC be embedded in the generated file. 
| -1 | --one | _flag_ | Aggregate all the generated html files into a single one.html file | 

## Preprocessors

### Overview

The tool has a number of preprocessors so you can include some custom processing. The current list is:

| Preprocessor | Arguments | Usage |
| --- | --- | --- | 
| code | _path_ | Include the code from specified path and highlight it with colors | 
| include | _path_ | include the file specified by path. If it is a markdown file it will be turn to html like the rest | 
| exec | _exp_ | execute the embeded clojure expression ;) and inserts the result into the file | 

### Usage 

Processors can be inserted into the markdown file, using the @@@ symbol before and after on the same line, like this:
    
![code](../doc/mark.png)

## About Pegdown

[Pegdown](https://github.com/sirthias/pegdown) is a markdown to html converter. It supports the core markdown syntax plus quite a few other extensions, notably those from [Multimarkdown](http://fletcherpenney.net/multimarkdown/).

The project examples can be found on [github](https://github.com/sirthias/pegdown/tree/master/src/test/resources) so have a look if you get stuck. 

## Cookbooks

Here a few one liners to generate the documentation you want. 

### One html file and one pdf file from markdown files. Embed the TOC

    java -jar medic.jar -d doc -x -o output -c doc/html -e -1
    
Note that the _-x_ flag will also empty the output folder before generating new files

### Generate one file for the TOC and parse all files to html separately

    java -jar medic.jar -d doc -x -o output -c doc/html
    
### Generate one file for the TOC and parse all files to html into one file

    java -jar medic.jar -d doc -x -o output -c doc/html -1
    