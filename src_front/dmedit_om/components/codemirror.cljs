(ns dmedit-om.components.codemirror
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]))

(defui CodemirrorComponent
  Object
  (render [this]
          (dom/div #js {:id "codemirror-target"}))

  (componentWillReceiveProps [this next-props]
                             (let [{:keys [w h]} (om/props this)]
                               (resize-codemirror w h))
                             )
  (componentDidMount [this]
                     (println "did mount")
                     (swap! app-state assoc :codemirror
                            (js/CodeMirror (gdom/getElement "codemirror-target")
                                           #js {:matchBrackets true :autoCloseBrackets true :lineWrapping true}))

                     (let [{:keys [w h]} (om/props this)]
                       (resize-codemirror w h))))

(def codemirror (om/factory CodemirrorComponent))
