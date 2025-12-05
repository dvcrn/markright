(ns electron.ipc
  (:require [cljs.core.async :as async]
            [cljs.reader :as reader]))

(def electron (js/require "electron"))
(def renderer? (some? (.-ipcRenderer electron)))
(def IPC (if renderer?
           (.-ipcRenderer electron)
           (.-ipcMain electron)))

(def IPC-CHANNEL "electron.ipc")

(defmulti process-call
  (fn [msg reply]
    (::action msg)))

(defmulti process-cast
  ::action)

(defonce *pending* (volatile! {}))
(def *ipc-target* (volatile! (when renderer? IPC)))

(defn set-target! [target]
  ;; renderer always talks to ipcRenderer, main sends to the window's webContents
  (if renderer?
    (vreset! *ipc-target* IPC)
    (vreset! *ipc-target* (.-webContents target))))

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
                (.send @*ipc-target* IPC-CHANNEL msg)))]
        (process-call msg reply-fn))
      :reply
      (let [{:keys [ref value success]} msg
            chan (get @*pending* ref)]
        ;; FIXME: chan might be nil
        (if success
          (do (async/put! chan value)
              (async/close! chan))
          (do (async/close! chan)
              (prn [:not-a-success msg])))
        ))))

(.on IPC IPC-CHANNEL process-msg)

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

        chan (async/chan 1)
        target @*ipc-target*]
    (vswap! *pending* assoc ref chan)

    (if target
      (.send target IPC-CHANNEL text)
      (do
        (println "IPC ERROR: target is nil/undefined in call for action" action)
        (async/close! chan)))
    chan))

(defn cast
  [action args]
  (let [msg (assoc args
              ::op :cast
              ::action action)
        text (pr-str msg)
        target @*ipc-target*]
    (if target
      (.send target IPC-CHANNEL text)
      (println "IPC ERROR: target is nil/undefined in cast for action" action))
    ))
