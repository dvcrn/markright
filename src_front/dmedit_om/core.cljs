(ns dmedit-om.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [figwheel.client :as fw :include-macros true]))

(enable-console-print!)

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'mount-root)

(defonce app-state (atom {}))

(defn resize-codemirror [w h]
  (let [codemirror (aget
                    (. js/document
                       (getElementsByClassName "CodeMirror")) 0)]
    (.setAttribute codemirror "style" (str "width:"w"px;height:"h"px;"))))

(defn codemirror [state owner]
  (reify
      om/IWillUpdate
    (will-update [this next-props next-state]
      (println next-props)
      (println next-state)
      (resize-codemirror (get-in state [:window :w]) (get-in state [:window :h])))
      om/IRenderState
    (render-state [this state]
      (dom/div nil))
    om/IDidMount
    (did-mount [_]
      (swap! app-state assoc :codemirror
             (js/CodeMirror (om/get-node owner)
              #js {:matchBrackets true :autoCloseBrackets true :lineWrapping true}))
      (resize-codemirror (get-in state [:window :w]) (get-in state [:window :h])))))

(defn mount-root []
  (om/root
   (fn [state owner]
     (reify om/IRender
       (render [_]
         (om/build codemirror state))))
   app-state
   {:target (. js/document
               (getElementById "app"))}))

(defn windowresize-handler
  [event]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    ;(om/set-state! app-state :window (hash-map :w w :h h))
    (swap! app-state assoc :window (hash-map :w w :h h))))

(defn get-text []
  (.getValue (@app-state :codemirror)))

(defn parse-markdown [code]
  (js/marked code))

(defn textchange-handler [event]
  (.log js/console (parse-markdown (get-text))))

(defonce resize-init
  (.addEventListener js/window "resize" windowresize-handler))

(windowresize-handler nil)
(mount-root)


