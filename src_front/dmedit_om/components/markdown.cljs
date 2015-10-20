(ns dmedit-om.components.markdown
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true])) 

(defui MarkdownComponent
  Object
  (render [this]
          (let [{:keys [app/html]} (om/props this)]
            (dom/div #js {:id "parsed-markdown"}
                     (dom/div #js {:className "markdown-body"
                                   :dangerouslySetInnerHTML #js {:__html html}})))))

(def markdown (om/factory MarkdownComponent))
