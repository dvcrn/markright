(ns dmedit-om.electron.menu
  (:require [dmedit-om.parser :as parser]))

(def remote (js/require "remote"))
(def menu (.require remote "menu"))
(def actions (.require remote "./actions"))
(def app-name "dmedit")

;; Functions
(defn reload! []
  (.reload (.getCurrentWindow remote)))

(defn toggle-devtools! []
  (.toggleDevTools (.getCurrentWindow remote)))

(defn open-file! []
   (let [filepath (aget (.open_file actions.core) 0)]
     (let [content (.read_file actions.core filepath)]
       (swap! parser/app-state assoc :app/force-overwrite true)
       (swap! parser/app-state assoc :app/text content))))

;; Menu structure
(def dmedit #js {:label "dmedit"
                 :submenu #js
                 [
                  #js {:label (str  "About " app-name)
                       :role "about"}

                  #js {:type "separator"}

                  #js {:label "Services"
                       :role "services"
                       :submenu #js []}

                  #js {:type "separator"}

                  #js {:label (str "Hide " app-name)
                       :accelerator "CmdOrCtrl+H"
                       :role "hide"}

                  #js {:label "Hide Others"
                       :accelerator "CmdOrCtrl+Shift+H"
                       :role "hideothers"}

                  #js {:label "Show All"
                       :role "unhide"}

                  #js {:type "separator"}

                  #js {:label "Quit"
                       :accelerator "CmdOrCtrl+Q"
                       :selector "terminate:"}
                  ]})

(def file #js {:label "File"
   :submenu #js
   [
    #js {:label "Open..."
         :accelerator "Command+O"
         :click open-file!}
    ]})


(def edit #js {:label "Edit"
               :submenu #js
               [
                #js {:label "Undo"
                     :accelerator "CmdOrCtrl+Z"
                     :role "undo"}

                #js {:label "Redo"
                     :accelerator "Shift+CmdOrCtrl+Z"
                     :role "redo"}

                #js {:type "separator"}

                #js {:label "Copy"
                     :accelerator "CmdOrCtrl+C"
                     :role "copy"}

                #js {:label "Paste"
                     :accelerator "CmdOrCtrl+V"
                     :role "paste"}

                #js {:label "Cut"
                     :accelerator "CmdOrCtrl+X"
                     :role "Cut"}

                #js {:label "Select All"
                     :accelerator "CmdOrCtrl+A"
                     :role "selectall"}

                ]})

(def develop #js {:label "Develop"
                  :submenu #js
                  [
                   #js {:label "Reload"
                        :accelerator "Command+R"
                        :click reload!}

                   #js {:label "Toggle DevTools"
                        :accelerator "Alt+Command+I"
                        :click toggle-devtools!}
                   ]})

(defn create-menu! []
  (.setApplicationMenu menu (.buildFromTemplate menu #js [dmedit file edit develop])))
