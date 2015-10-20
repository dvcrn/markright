(ns dmedit-om.components.codemirror
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]))

(defonce local-state (atom {}))

(defui CodemirrorComponent
  Object
  (render [this]
          (dom/div #js {:id "codemirror-target"}))
  (componentDidMount [this]
                     (let [codemirror 
                           (js/CodeMirror (gdom/getElement "codemirror-target")
                                          #js {:matchBrackets true :autoCloseBrackets true :lineWrapping true})]
                       (swap! local-state assoc :codemirror codemirror)

                       (let [{:keys [text-callback]} (om/props this)]
                         (.on codemirror "change"
                              #(text-callback (.getValue codemirror)))))))

(def codemirror (om/factory CodemirrorComponent))
