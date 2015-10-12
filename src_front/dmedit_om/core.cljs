(ns dmedit-om.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [figwheel.client :as fw :include-macros true]))

(enable-console-print!)

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'mount-root)

(defonce app-state (atom {:message "Hello om world!"}))

(defn mount-root []
  (om/root
   (fn [state owner]
     (reify om/IRender
       (render [_]
         (dom/h1 nil (:message state)))))
   app-state
   {:target (. js/document
               (getElementById "app"))}))

(defn init! []
  ;; (mount-root)
  (. js/CodeMirror fromTextArea (. js/document (getElementById "code")))
  (defn windowresize-handler
    [event]
    (let [w (.-innerWidth js/window)
          h (.-innerHeight js/window)]
      )))


(init!)
