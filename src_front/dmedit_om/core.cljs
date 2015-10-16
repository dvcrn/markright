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

(defonce app-state (atom {:codemirror "codemirror" :window {:w 0 :h 0} :foo "bar"}))

(defmulti read om/dispatch)
(defmethod read :foo
  [{:keys [state] :as env} key params]
  {:value (@state key)})

(defmethod read :codemirror
  [{:keys [state] :as env} key params]
  {:value (@state key)})

(defmethod read :window
  [{:keys [state] :as env} key params]
  {:value (@state key)})

(defmulti mutate om/dispatch)
(defmethod mutate 'window/size
  [{:keys [state]} _ {:keys [w h]}]
  {:action #(swap! state assoc  :window {:w w :h h})})

(defmethod mutate `codemirror
  [{:keys [state]} _ {:keys [codemirror]}]
  {:action #(swap! state assoc  :codemirror codemirror)})

(def reconciler
  (om/reconciler
   {:state app-state
    :parser (om/parser {:read read :mutate mutate})}))

(defui RootComponent
  static om/IQuery
  (query [this]
         (let [subquery (om/get-query CodemirrorComponent)]
           (into [] (concat subquery [:foo]))))
  Object
  (render [this]
          (codemirror (om/props this))))

(defn windowresize-handler
  [event]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    (om/transact! reconciler
                  `[(window/size {:w ~w :h ~h})])))

;; (defn get-text []
;;   (.getValue (@app-state :codemirror)))
;; 
;; (defn parse-markdown [code]
;;   (js/marked code))
;; 
;; (defn textchange-handler [event]
;;   (.log js/console (parse-markdown (get-text))))

(defonce resize-init
  (.addEventListener js/window "resize" windowresize-handler))

(windowresize-handler nil)

(om/add-root! reconciler RootComponent (gdom/getElement "app"))
