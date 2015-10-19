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
         '[:cm/size :cm :cm/text])
  Object
  (render [this]
          (dom/div #js {:id "codemirror-target"}))

  (componentWillReceiveProps [this next-props]
                             (let [{:keys [cm cm/size cm/text]} (om/props this)]
                               (.setValue (.getDoc cm) text)
                               (resize-codemirror (size :w) (size :h))))
  (componentDidMount [this]
                     (let [codemirror 
                           (js/CodeMirror (gdom/getElement "codemirror-target")
                                          #js {:matchBrackets true :autoCloseBrackets true :lineWrapping true})]
                       (om/transact! this `[(codemirror/instance {:codemirror ~codemirror})])

                       ;; resize the editor to full size
                       (let [{:keys [cm/size cm]} (om/props this)]
                         (resize-codemirror (size :w) (size :h))

                         (.on codemirror "change"
                              #(om/transact! this `[(codemirror/text
                                                     {:text ~(.getValue codemirror)})]))))))

;; (defn get-text []
  ;;(.getValue (@app-state :codemirror)))
 
;; (defn parse-markdown [code]
;;   (js/marked code))
;; 
;; (defn textchange-handler [event]
;;   (.log js/console (parse-markdown (get-text))))

(def codemirror (om/factory CodemirrorComponent))
