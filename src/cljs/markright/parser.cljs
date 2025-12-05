(ns markright.parser
  (:require [markright.state :refer [app-state]]
            [electron.ipc :as ipc]))

(def loaded true)

(defmethod ipc/process-cast :load-file
  [data]
  (swap! app-state assoc
         :app/text (data :content)
         :app/saved-text (data :content)
         :app/filepath (data :file)
         :app/force-overwrite true))

(defmethod ipc/process-cast :set-current-file
  [data]
  (swap! app-state assoc
         :app/filepath (data :file)
         :app/saved-text (data :content)))

(defmethod ipc/process-cast :set-saved-content
  [data]
  (swap! app-state assoc
         :app/saved-text (data :content)))

(defmethod ipc/process-call :get-is-saved
  [msg reply]
  (let [{:keys [app/text app/saved-text]} @app-state]
    (reply (= text saved-text))))

(defmethod ipc/process-call :get-current-content
  [msg reply]
  (reply (@app-state :app/text)))

(defmethod ipc/process-call :get-current-file
  [msg reply]
  (reply (@app-state :app/filepath)))
