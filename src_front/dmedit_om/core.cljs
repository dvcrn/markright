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

(defn windowresize-handler
  [event]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]

    (let [codemirror (aget
                      (. js/document
                         (getElementsByClassName "CodeMirror")) 0)]
      (.setAttribute codemirror "style" (str "width:"w"px;height:"h"px;")))))

(defn get-text []
  (.getValue (@app-state :codemirror)))

(defn parse-markdown [code]
  (js/marked code))

(defn textchange-handler [event]
  (.log js/console (parse-markdown (get-text))))

(defonce codemirror-init
  (->>
   (. js/CodeMirror fromTextArea
      (. js/document (getElementById "code"))
      (js-obj "lineWrapping" true))
   (swap! app-state assoc :codemirror)))

(defonce resize-init
  (.addEventListener js/window "resize" windowresize-handler))

(defonce change-init
  (.on (@app-state :codemirror) "change" textchange-handler))


(windowresize-handler nil)


