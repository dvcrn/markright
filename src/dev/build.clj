(ns build
  (:require [shadow.cljs.build :as cljs]
            [shadow.devtools.server :as devtools]
            [shadow.cljs.node :as node]
            [clojure.java.io :as io]
            ))

(defn main-dev []
  (-> (cljs/init-state)
      (cljs/find-resources-in-classpath)
      (cljs/find-resources "src/cljs")
      (node/configure
        {:main 'markright.main
         :output-to "node/app.js"})

      (cljs/watch-and-repeat!
        (fn [state modified]
          (-> state
              (node/compile)
              (node/flush)))))
  :done)

(defn ui-setup [opts]
  (-> (cljs/init-state)
      (cljs/set-build-options
        {:public-dir (io/file "node/ui/js")
         :public-path "js"})
      (cljs/set-build-options opts)
      (cljs/find-resources-in-classpath)
      (cljs/find-resources "src/cljs")
      (cljs/finalize-config)
      (cljs/configure-module :front '[markright.ui] #{})))

(defn ui-prod []
  (-> (ui-setup {:optimizations :simple})
      (cljs/compile-modules)
      (cljs/closure-optimize)
      (cljs/flush-modules-to-disk))
  :done)

(defn ui-dev []
  (-> (ui-setup {})
      (devtools/start-loop
        {}))
  :done)
