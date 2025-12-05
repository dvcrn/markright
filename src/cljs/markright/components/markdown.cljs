(ns markright.components.markdown
  (:require [reagent.core :as r]
            [goog.dom :as gdom]
            [markright.bootstrap]
            ["@tauri-apps/plugin-fs" :refer [readFile]]))

(def current-path (atom ""))

(defn fill-markdown []
  (let [md (. js/document (getElementById "parsed-markdown"))
        h (.-innerHeight js/window)]
    (when md (.setAttribute md "style" (str "height:" h "px;")))))

(defn generate-open-external-string [url]
  (str "window.openUrl('"
       (js/decodeURIComponent url)
       "'); return false;"))

(defn parse-codeblocks! []
  (let [tags (.getElementsByTagName js/document "code")]
    (doseq [tag (array-seq tags)]
      (if (.getAttribute tag "class")
        (.highlightBlock js/hljs tag)))))

(defn parse-urls! []
  (let [a-tags (.getElementsByTagName js/document "a")]
     (doseq [tag (array-seq a-tags)]
       (.setAttribute tag "onclick" (generate-open-external-string (.-href tag)))
       (.setAttribute tag "href" "#"))))

(defn load-local-image [img-elem full-path]
  (-> (readFile full-path)
      (.then (fn [content]
               (let [blob (js/Blob. #js [content])
                     url (js/URL.createObjectURL blob)]
                 (.setAttribute img-elem "src" url))))
      (.catch (fn [err]
                (js/console.error "Failed to load image:" full-path err)))))

(defn parse-images! [current-path]
  (let [img-tags (.getElementsByTagName js/document "img")]
    (doseq [tag (array-seq img-tags)]
      ;; To make sure we always use the real path for transforming
      ;; and not the already transformed one
      (if (= (.indexOf (.getAttribute tag "src") "http") -1)
        (do
          (if (not (.getAttribute tag "data-src"))
            (.setAttribute tag "data-src" (.getAttribute tag "src")))
          (let [full-path (str current-path "/" (.getAttribute tag "data-src"))]
            (load-local-image tag full-path)))))))

(defn post-render! []
  (parse-urls!)
  (parse-codeblocks!)
  (parse-images! @current-path))

(defn markdown-component [{:keys [html filepath]}]
  (r/create-class
   {:component-did-mount
    (fn [this]
      (.addEventListener js/window "resize" fill-markdown)
      (post-render!))

    :component-will-unmount
    (fn [this]
      (.removeEventListener js/window "resize" fill-markdown))

    :component-did-update
    (fn [this]
      (post-render!))

    :reagent-render
    (fn [{:keys [html filepath]}]
      (reset! current-path (if filepath (subs filepath 0 (.lastIndexOf filepath "/")) ""))
      [:div {:id "parsed-markdown"}
       [:div {:class-name "markdown-body"
              :dangerouslySetInnerHTML {:__html html}}]])}))

(defn markdown [props]
  [markdown-component props])
