(ns dmedit-om.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]
            [dmedit-om.components.codemirror :refer [codemirror]]))

(enable-console-print!)

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'mount-root)

(defonce app-state (atom {:window {:w 0 :h 0}}))

(defn read [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))

(defn mutate [{:keys [state] :as env} key params]
  (if (= `change-windowsize key)
    (let [{:keys [w h]} params]
      (println w)
      {:value {:w w :h h}
       :action #(swap! state assoc :window {:w w :h h})}
      )
    {:value :not-found}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser (om/parser {:read read :mutate mutate})}))


(defn resize-codemirror [w h]
  (let [codemirror (aget
                    (. js/document
                       (getElementsByClassName "CodeMirror")) 0)]
    (.setAttribute codemirror "style" (str "width:"w"px;height:"h"px;"))))


(defui RootComponent
  static om/IQuery
  (query [this]
         [:window])
  Object
  (render [this]
          (let [{:keys [window]} (om/props this)]
            (codemirror window))))

(def root (om/factory RootComponent))

(defn mount-root []
  (js/React.render (root) (gdom/getElement "app")))

(defn windowresize-handler
  [event]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    (om/transact! reconciler
                  `[(change-windowsize {:w ~w :h ~h})])))

(defn get-text []
  (.getValue (@app-state :codemirror)))

(defn parse-markdown [code]
  (js/marked code))

(defn textchange-handler [event]
  (.log js/console (parse-markdown (get-text))))

(defonce resize-init
  (.addEventListener js/window "resize" windowresize-handler))

(windowresize-handler nil)

(om/add-root! reconciler
              RootComponent (gdom/getElement "app"))
