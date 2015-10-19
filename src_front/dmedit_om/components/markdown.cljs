(ns dmedit-om.components.markdown
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true])) 


(defn resize-markdown [w h]
  (let [markdown (. js/document
                       (getElementById "parsed-markdown"))]
    (.setAttribute markdown "style" (str "width:"w"px;height:"h"px;"))))

(defn update-dom [dom]
  (let [markdown (. js/document
                    (getElementById "parsed-markdown"))]
    (aset markdown "innerHTML" dom)))

(defui MarkdownComponent
  static om/IQuery
  (query [this]
         '[:cm/text :cm/size])
  Object
  (render [this]
          (dom/div #js {:id "parsed-markdown"} "foooo"))
  (componentWillReceiveProps [this next-props]
                             (let [{:keys [cm/size cm/text]} (om/props this)]
                               (println (js/marked text))
                               (update-dom (js/marked text))
                               (resize-markdown (size :w) (size :h))))
  (componentDidMount [this]
                     (println "did mount")
                     (let [{:keys [cm/size cm]} (om/props this)]
                       (resize-markdown (size :w) (size :h)))))


(def markdown (om/factory MarkdownComponent))
