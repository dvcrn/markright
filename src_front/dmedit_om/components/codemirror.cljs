(ns dmedit-om.components.codemirror
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]))

(defonce local-state (atom {}))

(defn fill-codemirror [_]
  (let [cm (aget (. js/document (getElementsByClassName "CodeMirror")) 0)
        cmg (aget (. js/document (getElementsByClassName "CodeMirror-gutters")) 0)
        h (.-innerHeight js/window)]
    (.setAttribute cm "style" (str "height:"h"px;"))
    (.setAttribute cmg "style" (str "height:"h"px;"))))

(defui CodemirrorComponent
  Object
  (render [this]
          (dom/div #js {:id "codemirror-target"}))
  (componentDidMount [this]
                     (let [codemirror 
                           (js/CodeMirror (gdom/getElement "codemirror-target")
                                          #js {:matchBrackets true
                                               :autoCloseBrackets true
                                               :lineWrapping true
                                               :lineNumbers true
                                               })]
                       (swap! local-state assoc :codemirror codemirror)

                       (let [{:keys [app/text text-callback]} (om/props this)]
                         (.setValue (.getDoc codemirror) text)
                         (.on codemirror "change"
                              #(text-callback (.getValue codemirror)))))

                     (.addEventListener js/window "resize" fill-codemirror)
                     (fill-codemirror nil))
  (componentWillUnmount [this]
                        (.removeEventListener js/window "resize" fill-codemirror)))

(def codemirror (om/factory CodemirrorComponent))
