(ns dmedit-om.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]
            [dmedit-om.parser :as p]
            [dmedit-om.components.codemirror :refer [codemirror CodemirrorComponent]]
            [dmedit-om.components.markdown :refer [markdown MarkdownComponent]]))

(enable-console-print!)

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'mount-root)

(defui RootComponent
  static om/IQueryParams
  (params [this]
          {:codemirror-query (om/get-query CodemirrorComponent)
           :markdown-query (om/get-query MarkdownComponent)})
  static om/IQuery
  (query [this]
         '[{:codemirror-props ?codemirror-query}
           {:markdown-props ?markdown-query}])
  Object
  (render [this]
          (let [{:keys [codemirror-props markdown-props]} (om/props this)]
            (dom/div #js {:id "wrapper"}
                     (codemirror codemirror-props)
                     (markdown markdown-props))))
  (componentWillMount [this])
  (componentWillUnmount [this]))

(def reconciler
  (om/reconciler
   {:state (atom {:cm nil :cm/text ""})
    :parser (om/parser {:read p/read :mutate p/mutate})}))

(om/add-root! reconciler RootComponent (gdom/getElement "app"))
