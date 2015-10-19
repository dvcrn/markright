(ns dmedit-om.parser
  (:require [om.next :as om :refer-macros [defui]]))


(defmulti read om/dispatch)
(defmethod read :default
  [{:keys [state]} k _]
  (let [st @state]
    (find st k)
    (if-let [[_ v] (find st k)]
      {:value v}
      {:value :not-found})))

(defmulti mutate om/dispatch)
(defmethod mutate 'codemirror/instance
  [{:keys [state]} _ {:keys [codemirror]}]
  {:action #(swap! state assoc-in [:codemirror :instance] codemirror)})

(defmethod mutate 'codemirror/text
  [{:keys [state]} _ {:keys [text]}]
  {:action #(do
              (swap! state assoc-in [:codemirror :text] text)
              (swap! state assoc-in [:markdown :text] text))})
