(ns markright.bootstrap
  (:require ["react" :as react]
            ["react-dom" :as react-dom]
            ["highlight.js/lib/core" :as hljs]
            ["highlight.js/lib/languages/javascript" :as lang-js]
            ["highlight.js/lib/languages/xml" :as lang-xml]
            ["highlight.js/lib/languages/markdown" :as lang-md]
            ["highlight.js/lib/languages/css" :as lang-css]
            ["highlight.js/lib/languages/json" :as lang-json]
            ["highlight.js/lib/languages/bash" :as lang-bash]))

(set! js/React react)
(set! js/ReactDOM react-dom)

(.registerLanguage hljs "javascript" lang-js)
(.registerLanguage hljs "xml" lang-xml)
(.registerLanguage hljs "markdown" lang-md)
(.registerLanguage hljs "css" lang-css)
(.registerLanguage hljs "json" lang-json)
(.registerLanguage hljs "bash" lang-bash)

(set! js/hljs hljs)

(def marked (if (exists? js/marked) js/marked (js/require "marked")))
;; (set! js/marked (.-parse marked))

(when (exists? js/hljs)
  (.highlightAll js/hljs))
