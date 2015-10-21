(ns dmedit.core
  (:require [cljs.nodejs :as nodejs]))

(enable-console-print!)
(nodejs/enable-util-print!)

(def dialog (nodejs/require "dialog"))
(def fs (nodejs/require "fs"))

(defn ^:export open-dialog []
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

(defn ^:export write-file [filepath content]
  (.writeFileSync fs filepath content #js {:encoding "utf8"}))

(defn ^:export save-dialog []
  (.showSaveDialog dialog #js
                   {:filters #js [
                                  #js {:name "All Files"
                                       :extensions #js ["*"]}]}))

(defn noop [] nil)
(set! *main-cli-fn* noop)

(aset js/exports "core" dmedit.core)
