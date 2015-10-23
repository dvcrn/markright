(ns markright.components.markdown
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom])) 


(defn fill-markdown [_]
  (let [md (. js/document (getElementById "parsed-markdown"))
        h (.-innerHeight js/window)]
    (.setAttribute md "style" (str "height:"h"px;"))))

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
                        (.removeEventListener js/window "resize" fill-markdown)))

(def markdown (om/factory MarkdownComponent))
