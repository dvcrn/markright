(ns dmedit-om.components.codemirror
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]))

(defn resize-codemirror [w h]
  (let [codemirror (aget
                    (. js/document
                       (getElementsByClassName "CodeMirror")) 0)]
    (.setAttribute codemirror "style" (str "width:"w"px;height:"h"px;"))))

(defui CodemirrorComponent
  static om/IQuery
  (query [this]
         '[:codemirror :window])
  Object
  (render [this]
          (dom/div #js {:id "codemirror-target"}))

  (componentWillReceiveProps [this next-props]
                             (let [{:keys [window]} (om/props this)]
                               (resize-codemirror (window :w) (window :h))))
  (componentDidMount [this]
                     (let [codemirror 
                           (js/CodeMirror (gdom/getElement "codemirror-target")
                                          #js {:matchBrackets true :autoCloseBrackets true :lineWrapping true})]
                       (om/transact! this `[(codemirror {:codemirror ~codemirror})]))

                     (let [{:keys [window]} (om/props this)]
                       (resize-codemirror (window :w) (window :h)))))

;; (defn get-text []
;;   (.getValue (@app-state :codemirror)))
;; 
;; (defn parse-markdown [code]
;;   (js/marked code))
;; 
;; (defn textchange-handler [event]
;;   (.log js/console (parse-markdown (get-text))))

(def codemirror (om/factory CodemirrorComponent))
