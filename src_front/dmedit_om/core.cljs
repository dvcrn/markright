(ns dmedit-om.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]
            [dmedit-om.parser :as p]
            [dmedit-om.components.codemirror :refer [codemirror CodemirrorComponent]]
            [dmedit-om.components.markdown :refer [markdown MarkdownComponent]]
            ))

(enable-console-print!)

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'mount-root)

(defn resize-handler [this]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    (om/transact! this
                  `[(cm/size {:w ~(/ w 2) :h ~h})])
    (om/transact! this
                  `[(window/size {:w ~w :h ~h})])))

(defui RootComponent
  static om/IQueryParams
  (params [this]
          {:codemirror-query (om/get-query CodemirrorComponent)
           :markdown-query (om/get-query MarkdownComponent)})
  static om/IQuery
  (query [this]
         '[{:codemirror-props ?codemirror-query}
           :window/split
           {:markdown-props ?markdown-query}])
  Object
  (render [this]
          (let [{:keys [codemirror-props markdown-props window/split]} (om/props this)]
            (dom/div nil
                     (codemirror codemirror-props)
                     (markdown markdown-props)
                     )))
  (componentWillMount [this]
                      (.addEventListener js/window "resize"
                                         #(resize-handler this)))
  (componentWillUnmount [this]
                        (.removeEventListener js/window "resize")))

(def reconciler
  (om/reconciler
   {:state (atom {:window {:w 0 :h 0} :cm nil :cm/size {:w 0 :h 0} :cm/text "" :window/split true})
    :parser (om/parser {:read p/read :mutate p/mutate})}))

(resize-handler reconciler)

(om/add-root! reconciler RootComponent (gdom/getElement "app"))
