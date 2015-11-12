(ns markright.components.markdown
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]))

(def current-path (atom ""))

(defn fill-markdown [_]
  (let [md (. js/document (getElementById "parsed-markdown"))
        h (.-innerHeight js/window)]
    (.setAttribute md "style" (str "height:" h "px;"))))

(defn generate-open-external-string [url]
  (str "require('shell').openExternal('" (js/decodeURIComponent url) "')"))

(defn parse-codeblocks! []
  (let [tags (.getElementsByTagName js/document "code")]
    (doseq [tag (array-seq tags)]
      (if (.getAttribute tag "class")
        (.highlightBlock js/hljs tag)))))

(defn parse-urls! []
  (let [a-tags (.getElementsByTagName js/document "a")]
     (doseq [tag (array-seq a-tags)]
       (.setAttribute tag "onclick" (generate-open-external-string (.-href tag)))
       (.setAttribute tag "href" "#"))))

(defn parse-images! [current-path]
  (let [img-tags (.getElementsByTagName js/document "img")]
    (doseq [tag (array-seq img-tags)]
      (if (not (.getAttribute tag "data-src"))
        (.setAttribute tag "data-src" (.getAttribute tag "src")))
      (.setAttribute tag "src" (str "file://" current-path "/" (.getAttribute tag "data-src"))))))

(defui MarkdownComponent
  Object
  (render [this]
          (let [{:keys [app/html app/filepath]} (om/props this)]
            (swap! current-path #(subs filepath 0 (.lastIndexOf filepath "/")))
            (dom/div #js {:id "parsed-markdown"}
                     (dom/div #js {:className "markdown-body"
                                   :dangerouslySetInnerHTML #js {:__html html}}))))
  (componentWillMount [this]
                      (.addEventListener js/window "resize" fill-markdown))
  (componentWillUnmount [this]
                        (.removeEventListener js/window "resize" fill-markdown))
  (componentDidMount [this]
                     (parse-urls!)
                     (parse-codeblocks!)
                     (parse-images! @current-path))
  (componentDidUpdate [this prev-props prev-state]
                      (parse-urls!)
                      (parse-codeblocks!)
                      (parse-images! @current-path)))

(def markdown (om/factory MarkdownComponent))
