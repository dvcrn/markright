(ns dmedit-om.electron.menu
  (:require [dmedit-om.parser :as parser]))

(def remote (js/require "remote"))
(def menu (.require remote "menu"))
(def actions (.require remote "./actions"))

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
   [#js {:label "About dmedit"
         :selector "orderFrontStandardAboutPanel:"}
    #js {:label "Quit"
         :accelerator "Command+Q"
         :selector "terminate:"}]})

(def file #js {:label "File"
   :submenu #js
   [
    #js {:label "Open..."
         :accelerator "Command+O"
         :click open-file!}
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
  (.setApplicationMenu menu (.buildFromTemplate menu #js [dmedit file develop])))
