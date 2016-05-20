(ns electron.ipc
  (:require [cljs.nodejs :as node]
            [cljs.core.async :as async]
            [cljs.reader :as reader]))


;; FIXME: this is so incomplete ...

;; (def IPC (.-ipcMain (node/require "electron")))
(def IPC (atom nil))

(def ipc-renderer (.-ipcRenderer (node/require "electron")))
(def ipc-main (.-ipcMain (node/require "electron")))
(def IPC-CHANNEL "electron.ipc")

(.log js/console ipc-renderer)
(.log js/console ipc-main)

(defmulti process-call
  (fn [msg reply]
    (::action msg)))

(defmulti process-cast
  ::action)

(defonce *pending* (volatile! {}))

(defn process-msg [event arg]
  (let [msg (if (nil? arg) event arg)
        msg (reader/read-string msg)
        op (::op msg)]

    (case op
      :cast
      (process-cast msg)
      :call
      (let [ref (::ref msg)
            reply-fn
            (fn [value]
              (when (nil? value)
                (throw (ex-info "not allowed to reply with nil" {})))

              (let [res {::op :reply
                         :ref ref
                         :success true
                         :value value}
                    msg (pr-str res)]
                (.send @IPC IPC-CHANNEL msg)))]
        (process-call msg reply-fn))
      :reply
      (let [{:keys [ref value success]} msg
            chan (get @*pending* ref)]
        ;; FIXME: chan might be nil
        (if success
          (do (async/put! chan value)
              (async/close! chan))
          (do (async/close! chan)
              (prn [:not-a-success msg])))))))



(defn call
  [action args]
  {:pre [(keyword? action)
         (map? args)]}
  (let [ref (random-uuid)
        msg (assoc args
              ::op :call
              ::action action
              ::ref ref)
        text (pr-str msg)

        chan (async/chan 1)]
    (vswap! *pending* assoc ref chan)

    (.send @IPC IPC-CHANNEL text)
    chan))

(defn cast
  [action args]
  (let [msg (assoc args
              ::op :cast
              ::action action)
        text (pr-str msg)]
    (.send @IPC IPC-CHANNEL text)))


(defn use-main! []
    (.log js/console "Using main IPC")
    (reset! IPC ipc-main)
    (.log js/console @IPC)
    (.on @IPC IPC-CHANNEL process-msg))

(defn use-renderer! []
    (.log js/console "Using renderer IPC")
    (reset! IPC ipc-renderer)
    (.log js/console @IPC)
    (.on @IPC IPC-CHANNEL process-msg))

