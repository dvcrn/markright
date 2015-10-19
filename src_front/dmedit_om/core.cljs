(ns dmedit-om.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [figwheel.client :as fw :include-macros true]
            [dmedit-om.parser :as p]
            [dmedit-om.components.codemirror :as cm]
            [dmedit-om.components.markdown :as md]))

(enable-console-print!)

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'mount-root)

(defui RootComponent
  static om/IQuery
  (query [this]
         (let [cmq (om/get-query cm/CodemirrorComponent)
               mdq (om/get-query md/MarkdownComponent)]
               `[{:app ~cmq} {:app ~mdq}]))
  Object
  (render [this]
          (println (om/props this))
          (let [{:keys [component/codemirror component/markdown]} (om/props this)]
            (println codemirror)
            (println markdown)
            )
         ;;   (println "codemirror")
         ;;   (println codemirror)
         ;;   (dom/div #js {:id "wrapper"}
         ;;            (cm/codemirror codemirror)
         ;;            (md/markdown markdown)))
          ))

(def app-state (atom  {:app/text "mehh" :app/foo "mooh"}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser (om/parser {:read p/read :mutate p/mutate})}))

(om/add-root! reconciler RootComponent (gdom/getElement "app"))

(enable-console-print!)

;; (def init-data
;;   {:list/one [{:name "John" :points 0}
;;               {:name "Mary" :points 0}
;;               {:name "Bob"  :points 0}]
;;    :list/two [{:name "Mary" :points 0 :age 27}
;;               {:name "Gwen" :points 0}
;;               {:name "Jeff" :points 0}]})
;; 
;; ;; -----------------------------------------------------------------------------
;; ;; Parsing
;; 
;; (defmulti read om/dispatch)
;; 
;; (defn get-people [state key]
;;   (let [st @state]
;;     (into [] (map #(get-in st %)) (get st key))))
;; 
;; (defmethod read :list/one
;;   [{:keys [state] :as env} key params]
;;   ;; (println (get-people state key))
;;   {:value (get-people state key)})
;; 
;; (defmethod read :list/two
;;   [{:keys [state] :as env} key params]
;;   {:value (get-people state key)})
;; 
;; (defmulti mutate om/dispatch)
;; 
;; (defmethod mutate 'points/increment
;;   [{:keys [state]} _ {:keys [name]}]
;;   {:action
;;    (fn []
;;      (swap! state update-in
;;        [:person/by-name name :points]
;;        inc))})
;; 
;; (defmethod mutate 'points/decrement
;;   [{:keys [state]} _ {:keys [name]}]
;;   {:action
;;    (fn []
;;      (swap! state update-in
;;        [:person/by-name name :points]
;;        #(let [n (dec %)] (if (neg? n) 0 n))))})
;; 
;; ;; -----------------------------------------------------------------------------
;; ;; Components
;; 
;; (defui Person
;;   static om/Ident
;;   (ident [this {:keys [name]}]
;;     [:person/by-name name])
;;   static om/IQuery
;;   (query [this]
;;     '[:name :points])
;;   Object
;;   (render [this]
;;     ;; (println "Render Person" (-> this om/props :name))
;;     ;; (println (om/props this))
;;     (let [{:keys [points name foo] :as props} (om/props this)]
;;       (dom/li nil
;;         (dom/label nil (str name ", points: " points))
;;         (dom/button
;;           #js {:onClick
;;                (fn [e]
;;                  (om/transact! this
;;                    `[(points/increment ~props)]))}
;;           "+")
;;         (dom/button
;;           #js {:onClick
;;                (fn [e]
;;                  (om/transact! this
;;                    `[(points/decrement ~props)]))}
;;           "-")))))
;; 
;; (def person (om/factory Person {:keyfn :name}))
;; 
;; (defui ListView
;;   Object
;;   (render [this]
;;     (println "Render ListView" (-> this om/path first))
;;     (let [list (om/props this)]
;;       (apply dom/ul nil
;;         (map person list)))))
;; 
;; (def list-view (om/factory ListView))
;; 
;; (defui RootView
;;   static om/IQuery
;;   (query [this]
;;     (let [subquery (om/get-query Person)]
;;       `[{:list/one ~subquery} {:list/two ~subquery}]))
;;   Object
;;   (render [this]
;;     (println "Render RootView")
;;     (let [{:keys [list/one list/two]} (om/props this)]
;;       (apply dom/div nil
;;         [(dom/h2 nil "List A")
;;          (list-view one)
;;          (dom/h2 nil "List B")
;;          (list-view two)]))))
;; 
;; (def reconciler
;;   (om/reconciler
;;     {:state  init-data
;;      :parser (om/parser {:read read :mutate mutate})}))
;; 
;; (om/add-root! reconciler
;;   RootView (gdom/getElement "app"))
