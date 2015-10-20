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
(defmethod mutate 'app/html
  [{:keys [state]} _ {:keys [html]}]
  {:value [:app/html]
   :action #(swap! state assoc-in [:app/html] html)})

(defmethod mutate 'app/text
  [{:keys [state]} _ {:keys [text]}]
  {:value [:app/text]
   :action (swap! state assoc-in [:app/text] text)})
