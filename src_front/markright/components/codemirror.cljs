(ns markright.components.codemirror
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]))

(defonce local-state (atom {}))

(defn fill-codemirror [_]
  (let [cm (aget (. js/document (getElementsByClassName "CodeMirror")) 0)
        cmg (aget (. js/document (getElementsByClassName "CodeMirror-gutters")) 0)
        h (.-innerHeight js/window)]
    (.setAttribute cm "style" (str "height:"h"px;"))
    (.setAttribute cmg "style" (str "height:"h"px;"))))

(defui CodemirrorComponent
  Object
  (render [this]
          (dom/div #js {:id "codemirror-target"}))
  (componentWillReceiveProps [this next-props]
                             (let [{:keys [app/force-overwrite app/text]} next-props]
                               ;; Ignore overwriting if force-overwrite is not true
                               ;; This is because the cursor would jump if we overwrite
                               ;; the entire thing with every keypress. Not good, no no
                               (if force-overwrite
                                 (do
                                   (.setValue (.getDoc (@local-state :codemirror)) text)
                                   ((@local-state :overwrite-callback))))))
  (componentDidMount [this]
                     (let [codemirror 
                           (js/CodeMirror (gdom/getElement "codemirror-target")
                                          #js {:matchBrackets true
                                               :mode "gfm"
                                               :autoCloseBrackets true
                                               :lineWrapping true
                                               :lineNumbers true
                                               })]
                       (swap! local-state assoc :codemirror codemirror)

                       (let [{:keys [app/text text-callback overwrite-callback]} (om/props this)]
                         (swap! local-state assoc :overwrite-callback overwrite-callback)
                         (.setValue (.getDoc codemirror) text)
                         (.on codemirror "change"
                              #(text-callback (.getValue codemirror)))))

                     (.addEventListener js/window "resize" fill-codemirror)
                     (fill-codemirror nil))
  (componentWillUnmount [this]
                        (.removeEventListener js/window "resize" fill-codemirror)))

(def codemirror (om/factory CodemirrorComponent))
