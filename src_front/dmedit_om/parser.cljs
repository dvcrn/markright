(ns dmedit-om.parser
  (:require [om.next :as om :refer-macros [defui]]))


(defmulti read om/dispatch)
(defmethod read :default
  [{:keys [state selector]} k _]
  (let [st @state]
    {:value (select-keys st selector)}))

(defmulti mutate om/dispatch)
(defmethod mutate 'window/size
  [{:keys [state]} _ {:keys [w h]}]
  {:action #(swap! state assoc :window {:w w :h h})})

(defmethod mutate 'codemirror
  [{:keys [state]} _ {:keys [codemirror]}]
  {:action #(swap! state assoc :cm codemirror)})
