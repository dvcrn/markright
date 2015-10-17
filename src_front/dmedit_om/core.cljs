(ns dmedit-om.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]
            [dmedit-om.components.codemirror :refer [codemirror CodemirrorComponent]]
            ))

(enable-console-print!)

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'mount-root)

(defonce app-state (atom {:foo "bar" :window "window" :cm "mooo"}))

(defmulti read om/dispatch)
;;(defmethod read :app
;;  [{:keys [state] :as env} key params]
;;  (let [app (@app-state :app)]
;;    {:value (map #(hash-map (nth % 0) (select-keys app (nth % 1))) (seq params))}))

(defmethod read :default
  [{:keys [state selector]} k _]
  (let [st @state]
    {:value (select-keys st selector)}))

(defmulti mutate om/dispatch)
(defmethod mutate 'window/size
  [{:keys [state]} _ {:keys [w h]}]
  {:action #(swap! state assoc  :window {:w w :h h})})

(defmethod mutate `codemirror
  [{:keys [state]} _ {:keys [codemirror]}]
  {:action #(swap! state assoc :codemirror codemirror)})

(def reconciler
  (om/reconciler
   {:state app-state
    :parser (om/parser {:read read :mutate mutate})}))

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
            (codemirror codemirror-props))))

(defn windowresize-handler
  [event]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    (om/transact! reconciler
                  `[(window/size {:w ~w :h ~h})])))

(defonce resize-init
  (.addEventListener js/window "resize" windowresize-handler))

(windowresize-handler nil)

(om/add-root! reconciler RootComponent (gdom/getElement "app"))
