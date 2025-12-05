(ns markright.tauri
  (:require [markright.state :refer [app-state]]
            ["@tauri-apps/api/event" :as event]
            ["@tauri-apps/plugin-dialog" :as dialog]
            ["@tauri-apps/plugin-fs" :as fs]
            ["@tauri-apps/plugin-opener" :as opener]
            ["@tauri-apps/api/window" :as window]))

(defn set-title [title]
  (js/console.log "Setting title to:" title)
  (-> (window/getCurrentWindow)
      (.setTitle title)))

(defn open-url [url]
  (opener/open url))

(set! (.-openUrl js/window) open-url)

(defn read-file [path]
  (-> (fs/readTextFile path)
      (.then (fn [content]
               (swap! app-state assoc
                      :app/text content
                      :app/saved-text content
                      :app/filepath path
                      :app/force-overwrite true)))
      (.catch (fn [err] (js/console.error "Error reading file:" err)))))

(defn open-file []
  (js/console.log "open-file called")
  (-> (dialog/open #js {:multiple false
                        :filters #js [#js {:name "Markdown" :extensions #js ["md" "markdown" "txt"]}]})
      (.then (fn [selected]
               (when selected
                 (read-file selected))))
      (.catch (fn [err] (js/console.error "Error opening dialog:" err)))))

(defn save-file-as []
  (js/console.log "save-file-as called")
  (-> (dialog/save #js {:filters #js [#js {:name "Markdown" :extensions #js ["md"]}]})
      (.then (fn [path]
               (when path
                 (let [content (:app/text @app-state)]
                   (-> (fs/writeTextFile path content)
                       (.then (fn []
                                (swap! app-state assoc
                                       :app/saved-text content
                                       :app/filepath path)))
                       (.catch (fn [err] (js/console.error "Error writing file:" err))))))))
      (.catch (fn [err] (js/console.error "Error saving dialog:" err)))))

(defn save-file []
  (js/console.log "save-file called")
  (let [path (:app/filepath @app-state)]
    (if (or (nil? path) (= "" path))
      (save-file-as)
      (let [content (:app/text @app-state)]
        (-> (fs/writeTextFile path content)
            (.then (fn []
                     (swap! app-state assoc :app/saved-text content)))
            (.catch (fn [err] (js/console.error "Error writing file:" err))))))))

(defn open-github []
  (open-url "https://github.com/dvcrn/markright"))

(defn init-listeners []
  (event/listen "menu-open" open-file)
  (event/listen "menu-save" save-file)
  (event/listen "menu-save_as" save-file-as)
  (event/listen "menu-github" open-github))
