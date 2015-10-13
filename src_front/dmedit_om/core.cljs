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
  )

(defn windowresize-handler
  [event]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]

    (let [codemirror (aget
                      (. js/document
                         (getElementsByClassName "CodeMirror")) 0)]
      (.setAttribute codemirror "style" (str "width:"w"px;height:"h"px;")))))

(defonce event-init (.addEventListener js/window "resize" windowresize-handler))
(defonce codemirror-init
  (. js/CodeMirror fromTextArea
     (. js/document (getElementById "code"))
     (js-obj "lineWrapping" true)))

(windowresize-handler nil)

(init!)
