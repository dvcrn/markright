(ns markright.ui
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [markright.parser :as p]
            [markright.components.codemirror :as cm]
            [markright.components.markdown :as md]
            [cljs.core.async :as async :refer [chan put! pub sub unsub]]))

(defui RootComponent
  static om/IQuery
  (query [this]
    '[:app/text :app/force-overwrite :app/filepath :app/saved-text])
  Object
  (componentDidMount [this]
                     ;; super mega hack
                     ;; FIXME: remove me
                     (go-loop []
                       (let [fx (<! p/root-channel)]
                         (fx this))
                       (recur)))
  (componentWillMount [this] (.setOptions js/marked #js {:gfm true}))
  (componentWillReceiveProps [this next-props])
  (render [this]
    (let [{:keys [app/text app/html app/force-overwrite app/filepath app/saved-text]} (om/props this)]
      (if (not (= 0 (.-length filepath)))
        (if (= text saved-text)
          (set! (.-title js/document) filepath)
          (set! (.-title js/document) (str "* " filepath))))

      (dom/div #js {:id "wrapper"}
        (cm/codemirror {:app/force-overwrite force-overwrite
                        :app/text text
                        :text-callback #(om/transact! this `[(app/text {:text ~%})])
                        :overwrite-callback #(om/transact! this `[(app/transact-overwrite)])})
        (md/markdown {:app/html (js/marked text)})))))

(om/add-root! p/reconciler RootComponent (gdom/getElement "app"))


