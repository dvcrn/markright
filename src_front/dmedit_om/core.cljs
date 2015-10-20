(ns dmedit-om.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]
            [dmedit-om.parser :as p]
            [dmedit-om.components.codemirror :as cm]
            [dmedit-om.components.markdown :as md]))

(enable-console-print!)

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'mount-root)

(defui RootComponent
  static om/IQuery
  (query [this]
         '[:app/text :app/html])
  Object
  (render [this]
          (let [{:keys [app/text app/html]} (om/props this)]

            (dom/div #js {:id "wrapper"}
                     (cm/codemirror {:app/text text
                                     :text-callback #(om/transact! this `[(app/text {:text ~%})])})
                     (md/markdown {:app/html (js/marked text)})))))

(def app-state (atom  {:app/text "## This is a GFM markdown editor"}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser (om/parser {:read p/read :mutate p/mutate})}))

(om/add-root! reconciler RootComponent (gdom/getElement "app"))
