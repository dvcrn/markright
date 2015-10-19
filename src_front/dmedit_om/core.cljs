(ns dmedit-om.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]
            [dmedit-om.parser :as p]
            [dmedit-om.components.codemirror :refer [codemirror CodemirrorComponent]]))

(enable-console-print!)

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'mount-root)

(defn windowresize-handler [this]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    (om/transact! this
                  `[(window/size {:w ~w :h ~h})])))

(defui RootComponent
  static om/IQueryParams
  (params [this]
          {:codemirror-query (om/get-query CodemirrorComponent)})
  static om/IQuery
  (query [this]
         '[{:codemirror-props ?codemirror-query}])
  Object
  (render [this]
          (let [{:keys [codemirror-props]} (om/props this)]
            (codemirror codemirror-props)))
  (componentWillMount [this]
                      (.addEventListener js/window "resize" #(windowresize-handler this)))
  (componentWillUnmount [this]
                        (.removeEventListener js/window "resize")))

(def reconciler
  (om/reconciler
   {:state (atom {:window {:w 0 :h 0}})
    :parser (om/parser {:read p/read :mutate p/mutate})}))

(windowresize-handler reconciler)

(om/add-root! reconciler RootComponent (gdom/getElement "app"))
