(ns markright.ui
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [goog.dom :as gdom]
            [markright.bootstrap]
            [markright.parser :as parser]
            [markright.state :refer [app-state]]
            [markright.components.codemirror :as cm]
            [markright.components.markdown :as md]
            [markright.tauri :as tauri]
            [cljs.core.async :as async :refer [chan put! pub sub unsub <!]]))

;; Ensure React/ReactDOM globals for Om/cljsjs interop
;; Moved to markright.bootstrap

(defn root-component []
  (let [{:keys [app/text app/html app/force-overwrite app/filepath app/saved-text]} @app-state]
    (if (not (= 0 (.-length filepath)))
      (if (= text saved-text)
        (set! (.-title js/document) filepath)
        (set! (.-title js/document) (str "* " filepath))))

    [:div {:id "wrapper"}
     [cm/codemirror {:force-overwrite force-overwrite
                     :text text
                     :text-callback #(swap! app-state assoc :app/text %)
                     :overwrite-callback #(swap! app-state assoc :app/force-overwrite false)}]
     [md/markdown {:html (js/marked.parse text) :filepath filepath}]]))

(defn init []
  ;; Force loading of parser namespace
  (when parser/loaded
    (rdom/render [root-component] (gdom/getElement "app"))
    (.setOptions js/marked #js {:gfm true})
    (tauri/init-listeners)
    (go
      ;; (let [backend-state (<! (ipc/call :backend-state {}))]
      ;;   (when (not (nil? (backend-state :content)))
      ;;     (swap! app-state assoc
      ;;            :app/text (backend-state :content)
      ;;            :app/saved-text (backend-state :content)
      ;;            :app/filepath (backend-state :filepath)
      ;;            :app/force-overwrite true)))
      )))

(init)
