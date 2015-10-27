(ns markright.main
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [cljs.nodejs :as nodejs]
            [electron.ipc :as ipc]
            [cljs.core.async :as async]))

(def *win* (atom nil))

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
                                    :extensions ["*"]}]})))
        content (.readFileSync fs file #js {:encoding "utf8"})]

    (ipc/cast :set-current-file {:file file
                                 :content content}))

  )

(defn save-file-as! []
  (js/console.log "FIXME: save-file-as!"))

(defn save-file! []
  (go (let [content (<! (ipc/call :get-current-content {}))]
        (js/console.log "save-file!" content)
        )))

;; Menu structure
(def dmedit
  {:label "dmedit"
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
      )))
