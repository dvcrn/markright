(ns markright.components.markdown
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]))

(defn fill-markdown [_]
  (let [md (. js/document (getElementById "parsed-markdown"))
        h (.-innerHeight js/window)]
    (.setAttribute md "style" (str "height:" h "px;"))))

(defn generate-open-external-string [url]
  (str "require('shell').openExternal('" (js/decodeURIComponent url) "')"))

(defn parse-codeblocks! []
  (let [tags (.getElementsByTagName js/document "code")]
    (doseq [tag (array-seq tags)]
      (.highlightBlock js/hljs tag))))

(defn parse-urls! []
  (let [a-tags (.getElementsByTagName js/document "a")]
     (doseq [tag (array-seq a-tags)]
       (.setAttribute tag "onclick" (generate-open-external-string (.-href tag)))
       (.setAttribute tag "href" "#"))))

(defui MarkdownComponent
  Object
  (render [this]
          (let [{:keys [app/html]} (om/props this)]
            (dom/div #js {:id "parsed-markdown"}
                     (dom/div #js {:className "markdown-body"
                                   :dangerouslySetInnerHTML #js {:__html html}}))))
  (componentWillMount [this]
                      (.addEventListener js/window "resize" fill-markdown))
  (componentWillUnmount [this]
                        (.removeEventListener js/window "resize" fill-markdown))
  (componentDidMount [this]
                     (parse-urls!)
                     (parse-codeblocks!))
  (componentDidUpdate [this prev-props prev-state]
                      (parse-urls!)
                      (parse-codeblocks!)))

(def markdown (om/factory MarkdownComponent))
