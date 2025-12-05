(ns markright.state
  (:require [reagent.core :as r]))

(defonce app-state (r/atom {:app/text ""
                            :app/force-overwrite false
                            :app/filepath ""
                            :app/saved-text ""
                            :app/html ""}))
