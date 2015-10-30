(ns markright.main
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [cljs.nodejs :as nodejs]
            [electron.ipc :as ipc]
            [cljs.core.async :as async]
            [clojure.string :refer [split]])
  (:import [goog.net XhrIo]))

(def *win* (atom nil))

(def http (nodejs/require "http"))
(def https (nodejs/require "https"))
(def path (nodejs/require "path"))
(def BrowserWindow (nodejs/require "browser-window"))
(def crash-reporter (nodejs/require "crash-reporter"))
(def dialog (nodejs/require "dialog"))

(def app (nodejs/require "app"))
(def shell (nodejs/require "shell"))
(def fs (nodejs/require "fs"))
(def process (nodejs/require "process"))

(def menu (nodejs/require "menu"))

(def app-name "something")

;; Functions
(defn reload! []
  (.reload @*win*))

(defn toggle-devtools! []
  (.toggleDevTools @*win*))

(defn open-url! [url]
  (.openExternal shell url))

(defn write-file [filepath content]
  (.writeFileSync fs filepath content #js {:encoding "utf8"}))

(defn save-dialog []
  (.showSaveDialog dialog #js
      {:filters #js [#js {:name "All Files"
                          :extensions #js ["*"]}]}))

(defn open-file! []
  (let [file (first (.showOpenDialog dialog
                      ;; way too many #js for my taste
                      (clj->js
                        {:properties ["openFile"]
                         :filters [{:name "Markdown"
                                    :extensions ["md" "markdown" "txt"]}
                                   {:name "All Files"
                                    :extensions ["*"]}]})))]
    (if (not (nil? file))
      (ipc/cast :load-file {:file file
                            :content (.readFileSync fs file #js {:encoding "utf8"})}))))

(defn save-file-as! []
  (go (let [file-path (save-dialog)
            content (<! (ipc/call :get-current-content {}))]
         (if (not (nil? file-path))
           (do
             (write-file file-path content)
             (ipc/cast :set-current-file {:file file-path
                                          :content content}))))))

(defn save-file! []
  (go (let [content (<! (ipc/call :get-current-content {}))
            filepath (<! (ipc/call :get-current-file {}))]
        (.-length filepath)
        (if (= 0 (.-length filepath))
          (save-file-as!)
          (do
            (write-file filepath content)
            (ipc/cast :set-saved-content {:content content}))))))

;; Menu structure
(def dmedit
  {:label "MarkRight"
   :submenu
   [{:label (str "About " app-name)
     :role "about"}

    {:type "separator"}

    {:label "Services"
     :role "services"
     :submenu []}

    {:type "separator"}

    {:label (str "Hide " app-name)
     :accelerator "CmdOrCtrl+H"
     :role "hide"}

    {:label "Hide Others"
     :accelerator "CmdOrCtrl+Shift+H"
     :role "hideothers"}

    {:label "Show All"
     :role "unhide"}

    {:type "separator"}

    {:label "Quit"
     :accelerator "CmdOrCtrl+Q"
     :selector "terminate:"}]})

(def file
  {:label "File"
   :submenu
   [{:label "Open..."
     :accelerator "CmdOrCtrl+O"
     :click open-file!}

    {:label "Save"
     :accelerator "CmdOrCtrl+S"
     :click save-file!}

    {:label "Save as..."
     :accelerator "CmdOrCtrl+Shift+S"
     :click save-file-as!}
    ]})

(def edit
  {:label "Edit"
   :submenu
   [{:label "Undo"
     :accelerator "CmdOrCtrl+Z"
     :role "undo"}

    {:label "Redo"
     :accelerator "Shift+CmdOrCtrl+Z"
     :role "redo"}

    {:type "separator"}

    {:label "Copy"
     :accelerator "CmdOrCtrl+C"
     :role "copy"}

    {:label "Paste"
     :accelerator "CmdOrCtrl+V"
     :role "paste"}

    {:label "Cut"
     :accelerator "CmdOrCtrl+X"
     :role "Cut"}

    {:label "Select All"
     :accelerator "CmdOrCtrl+A"
     :role "selectall"}

    ]})

(def window
  {:label "Window"
   :role "window"
   :submenu
   [{:label "Minimize"
     :accelerator "CmdOrCtrl+M"
     :role "minimize"}

    {:label "Close"
     :accelerator "CmdOrCtrl+W"
     :role "close"}]})

(def develop
  {:label "Develop"
   :submenu
   [{:label "Reload"
     :accelerator "CmdOrCtrl+R"
     :click reload!}

    {:label "Toggle DevTools"
     :accelerator "Alt+CmdOrCtrl+I"
     :click toggle-devtools!}
    ]})

(def help
  {:label "Help"
   :role "help"
   :submenu
   [{:label "dmedit on Github"
     :click #(open-url! "https://github.com/dvcrn/dmedit")}
    {:label "@davicorn (Twitter)"
     :click #(open-url! "https://twitter.com/davicorn")}
    ]})

(defn create-menu! []
  (.setApplicationMenu
    menu
    (.buildFromTemplate menu
      (clj->js
        [(when (= (.-platform process) "darwin")
           dmedit)
         file
         edit
         window
         develop
         help]))))

(def index (str "file://" (.resolve path (.getAppPath app) "ui" "index.html")))

(defn open-window! []
  (when (nil? @*win*)
    (let [win (BrowserWindow. (clj->js {:width 1200 :height 600}))]
      (reset! *win* win)
      (.loadUrl win index)
      (ipc/set-target! win)
      (.openDevTools win)
      (.on win "closed" (fn [] (reset! *win* nil))))))

(defn is-newer? [current remote]
  (let [first (split current #"\.")
        second (split remote #"\.")
        weight [10000 1000 100]]

    (->>
     [first second]
     (map (fn [group]
            (map #(->
                   %
                   (group)
                   (js/parseInt)
                   (* (weight %)))
                 [0 1 2])))
     (map #(reduce + %))
     (reduce >))))

(defn check-update! []
  (let [package (-> (.readFileSync fs "./package.json" #js {:encoding "utf8"})
                    (JSON/parse))]
    ;; xhr call
    ;; https://raw.githubusercontent.com/dvcrn/markright/master/node/package.json
    (.end
     (.request https #js {:host "raw.githubusercontent.com"
                         :path "/dvcrn/markright/master/node/package.json"
                         :port 443}
               (fn [response]
                 (let [data (atom {:data (str "")})]
                   (.on response "data" #(swap! data assoc :data (str (@data :data) %)))
                   (.on response "end" (fn []
                                         (let [remote-package  (JSON/parse (@data :data))
                                               latest-version (.-version remote-package)
                                               current-version (.-version package)]
                                           (if (is-newer? latest-version current-version)
                                             (.showMessageBox dialog #js {:type "info"
                                                                          :title "Update Available"
                                                                          :message "Hey! There is a new version of MarkRight available. You really should download it :)"
                                                                          :buttons #js ["Ok!"]})))))))))))

(defn main []
  (.start crash-reporter)

  ;; error listener
  (.on nodejs/process "error"
    (fn [err] (.log js/console err)))

  ;; window all closed listener
  (.on app "window-all-closed"
    (fn [] (if (not= (.-platform nodejs/process) "darwin")
             (.quit app))))

  (.on app "activate" open-window!)
  (.on app "ready"
    (fn []
      (create-menu!)
      (open-window!)
      (check-update!)
      )))
