(ns markright.dialogs)

(def dialog (.-dialog (js/require "electron")))

(defn unsaved-changes-dialog [win]
  (.showMessageBoxSync dialog
                       win
                       #js {:type "question"
                            :title "Unsaved changes"
                            :message "Unsaved changes"
                            :detail "There are unsaved changes. Are you sure you want to continue? Your changes will be lost."
                            :buttons #js ["Continue" "Cancel"]}))

(defn error-dialog [win title text]
  (.showMessageBoxSync dialog
                       win
                       #js {:type "error"
                            :title title
                            :message title
                            :detail text
                            :buttons #js ["Ok"]}))

(defn save-dialog [win]
  (.showSaveDialogSync dialog
                       win
                       #js {:filters #js [#js {:name "Markdown"
                                               :extensions #js ["md"]}]}))
(defn open-dialog [win]
  (.showOpenDialogSync dialog
                       win
                       (clj->js
                        {:properties ["openFile"]
                         :filters [{:name "Markdown"
                                    :extensions ["md" "markdown" "txt"]}
                                   {:name "All Files"
                                    :extensions ["*"]}]})))


(defn update-dialog [win current-version latest-version]
  (.showMessageBoxSync dialog
                       win
                       #js {:type "info"
                            :title "Update Available"
                            :message "Update Available"
                            :detail (str "Hi! MarkRight " latest-version " is available. You are currently running " current-version ".")
                            :buttons #js ["Ok" "Go to download"]}))
