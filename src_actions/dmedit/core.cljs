(ns dmedit.core
  (:require [cljs.nodejs :as nodejs]))

(enable-console-print!)
(nodejs/enable-util-print!)

(def dialog (nodejs/require "dialog"))
(def fs (nodejs/require "fs"))

(defn ^:export open-file []
  (.showOpenDialog dialog #js
                   {:properties #js ["openFile"]
                    :filters #js [
                                  #js {:name "Markdown"
                                       :extensions #js ["md" "markdown" "txt"]}
                                  #js {:name "All Files"
                                       :extensions #js ["*"]}
                                  ]}))

(defn ^:export read-file [filepath]
  (.readFileSync fs filepath #js {:encoding "utf8"}))

(defn noop [] nil)
(set! *main-cli-fn* noop)

(aset js/exports "core" dmedit.core)
