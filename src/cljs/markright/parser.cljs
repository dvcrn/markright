(ns markright.parser
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [markright.state :refer [app-state]]
            [electron.ipc :as ipc]
            [cljs.core.async :as async :refer [chan put! pub sub unsub <! >!]]))

;; This namespace is kept for IPC handling compatibility
;; but logic is moved to direct state manipulation

(defonce root-channel (chan))

(ipc/on :open-file
        (fn [data]
          (swap! app-state assoc
                 :app/text (data :content)
                 :app/saved-text (data :content)
                 :app/filepath (data :filepath)
                 :app/force-overwrite true)))

(ipc/on :save-file
        (fn [data]
          (let [state @app-state]
            (ipc/cast :save-file {:content (state :app/text)
                                  :filepath (state :app/filepath)}))))

(ipc/on :save-file-success
        (fn [data]
          (swap! app-state assoc :app/saved-text (@app-state :app/text))))

(ipc/on :save-as-file
        (fn [data]
          (let [state @app-state]
            (ipc/cast :save-as-file {:content (state :app/text)}))))
