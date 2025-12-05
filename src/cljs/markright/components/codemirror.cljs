(ns markright.components.codemirror
  (:require [reagent.core :as r]
            [goog.dom :as gdom]
            [markright.bootstrap]))

(defn fill-codemirror []
  (let [cm (aget (. js/document (getElementsByClassName "CodeMirror")) 0)
        cmg (aget (. js/document (getElementsByClassName "CodeMirror-gutters")) 0)
        h (.-innerHeight js/window)]
    (when cm (.setAttribute cm "style" (str "height:" h "px;")))
    (when cmg (.setAttribute cmg "style" (str "height:" h "px;")))))

(defn codemirror-component [{:keys [text force-overwrite text-callback overwrite-callback]}]
  (let [cm-instance (atom nil)]
    (r/create-class
     {:component-did-mount
      (fn [this]
        (let [cm (js/CodeMirror (gdom/getElement "codemirror-target")
                                #js {:matchBrackets true
                                     :mode "spell-checker"
                                     :backdrop "gfm"
                                     :autoCloseBrackets true
                                     :lineWrapping true
                                     :lineNumbers true})]
          (reset! cm-instance cm)
          (.setValue (.getDoc cm) text)
          (.on cm "change" #(text-callback (.getValue cm)))
          (.addEventListener js/window "resize" fill-codemirror)
          (fill-codemirror)))

      :component-will-unmount
      (fn [this]
        (.removeEventListener js/window "resize" fill-codemirror))

      :component-did-update
      (fn [this]
        (let [{:keys [text force-overwrite]} (r/props this)]
          (when (and force-overwrite @cm-instance)
            (.setValue (.getDoc @cm-instance) text)
            (overwrite-callback))))

      :reagent-render
      (fn []
        [:div {:id "codemirror-target"}])})))

(defn codemirror [props]
  [codemirror-component props])
