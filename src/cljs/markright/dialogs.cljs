(ns markright.dialogs
  (:require [cljs.nodejs :as nodejs]))

(def dialog (nodejs/require "dialog"))

(defn unsaved-changes-dialog [win]
  (.showMessageBox dialog
                   win
                   #js {:type "question"
                        :title "Unsaved changes"
                        :message "There are unsaved changes. Are you sure you want to close this window?"
                        :buttons #js ["Close" "Cancel"]}))

(defn save-dialog [win]
  (.showSaveDialog dialog
                   win
                   #js {:filters #js [#js {:name "All Files"
                                           :extensions #js ["*"]}]}))
(defn open-dialog [win]
  (.showOpenDialog dialog
                   win
                   (clj->js
                    {:properties ["openFile"]
                     :filters [{:name "Markdown"
                                :extensions ["md" "markdown" "txt"]}
                               {:name "All Files"
                                :extensions ["*"]}]})))


(defn update-dialog [win]
  (.showMessageBox dialog
                   win
                   #js {:type "info"
                        :title "Update Available"
                        :message "Hey! There is a new version of MarkRight available. You really should download it :)"
                        :buttons #js ["Ok!"]}))
