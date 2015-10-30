(ns markright.ui
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [markright.parser :as p]
            [markright.components.codemirror :as cm]
            [markright.components.markdown :as md]
            ))

(defui RootComponent
  static om/IQuery
  (query [this]
    '[:app/text :app/force-overwrite :app/filepath :app/saved-text])
  Object
  (componentWillMount [this]
    (.setOptions js/marked #js {:gfm true}))

  (componentWillReceiveProps [this next-props]
    (let [{:keys [app/filepath app/text]} next-props]
      (if (not (= 0 (.-length filepath)))
        (set! (.-title js/document) filepath))))

  (render [this]
    (let [{:keys [app/text app/html app/force-overwrite]} (om/props this)]
      (dom/div #js {:id "wrapper"}
        (cm/codemirror {:app/force-overwrite force-overwrite
                        :app/text text
                        :text-callback #(om/transact! this `[(app/text {:text ~%})])
                        :overwrite-callback #(om/transact! this `[(app/transact-overwrite)])})
        (md/markdown {:app/html (js/marked text)})))))

(om/add-root! p/reconciler RootComponent (gdom/getElement "app"))


