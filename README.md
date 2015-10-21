# David's Markdown Editor

a minimalistic editor for github flavored markdown documents

![screenshot](https://raw.githubusercontent.com/dvcrn/dmedit/master/resources/markdown.gif)

## Download

Here are the current binaries: 

- [Mac](https://github.com/dvcrn/dmedit/releases/download/0.1.0/dmedit.app.zip)

## Building

dmedit is written in clojurescript. To build, make sure you have clojure and leiningen installed on your system. 

1. Clone this project
2. `npm install` and `bower install`
3. `lein cljsbuild once` 

For development, add `src_dev` as `:frontend` source folder and run `lein figwheel` for awesome live coding experience :)
