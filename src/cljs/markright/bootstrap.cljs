(ns markright.bootstrap
  (:require ["react" :as react]
            ["react-dom" :as react-dom]
            ["highlight.js" :as hljs]))

(set! js/React react)
(set! js/ReactDOM react-dom)
(set! js/hljs hljs)

(def marked (js/require "marked"))
(set! js/marked (.-parse marked))

(when (exists? js/hljs)
  (.initHighlightingOnLoad js/hljs))
