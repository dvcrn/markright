(ns dmedit-om.parser
  (:require [om.next :as om :refer-macros [defui]]))

(def app-state (atom  {:app/text "## Welcome to dmedit\n\nThis is a minimalistic GFM markdown editor written in om.next.\n\nChanges to the document will be reflected in real time on the right ->\n\nPerfect for writing READMEs :)"}))

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

(defmethod mutate 'app/transact-overwrite
  [{:keys [state]} _ {:keys [text]}]
  {:value [:app/force-overwrite]
   :action (swap! state assoc-in [:app/force-overwrite] false)})

(def reconciler
  (om/reconciler
   {:state app-state
    :parser (om/parser {:read read :mutate mutate :force-overwrite false})}))
