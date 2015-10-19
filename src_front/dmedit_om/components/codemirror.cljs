(ns dmedit-om.components.codemirror
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]))

(defui CodemirrorComponent
  static om/IQuery
  (query [this]
         '[:instance :text])
  Object
  (render [this]
          (dom/div #js {:id "codemirror-target"}))

  ;; (componentWillReceiveProps [this next-props]
  ;;                            (let [{:keys [cm cm/size cm/text]} (om/props this)]
  ;;                              (.setValue (.getDoc cm) text)))
  (componentDidMount [this]
                     (println "inside codemirror mount")
                     (println (om/props this))
                     ;; (let [codemirror 
                     ;;       (js/CodeMirror (gdom/getElement "codemirror-target")
                     ;;                      #js {:matchBrackets true :autoCloseBrackets true :lineWrapping true})]
                     ;;   (om/transact! this `[(codemirror/instance {:codemirror ~codemirror})])
                     ;;   (.on codemirror "change"
                     ;;        #(om/transact! this `[(codemirror/text
                     ;;                               {:text ~(.getValue codemirror)})])))
                     ))

;; (defn get-text []
  ;;(.getValue (@app-state :codemirror)))
 
;; (defn parse-markdown [code]
;;   (js/marked code))
;; 
;; (defn textchange-handler [event]
;;   (.log js/console (parse-markdown (get-text))))

(def codemirror (om/factory CodemirrorComponent))
