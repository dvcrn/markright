(defproject dmedit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145" :exclusions [org.apache.ant/ant]]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om "1.0.0-alpha3"]
                 [com.cemerick/piggieback "0.2.1"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [figwheel "0.4.0"]]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-externs "0.1.3"]
            [lein-figwheel "0.4.0" :exclusions [org.clojure/core.cache]]]
  :source-paths ["src_tools"]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {
              :builds {:main {:id "dmedit"
                              :figwheel true
                              :source-paths ["src"]
                              :incremental true
                              :jar true
                              :assert true
                              :compiler {:output-to "app/js/cljsbuild-main.js"
                                         :externs ["app/js/externs.js"
                                                   "node_modules/closurecompiler-externs/path.js"
                                                   "node_modules/closurecompiler-externs/process.js"]
                                         :warnings true
                                         :elide-asserts true
                                         :target :nodejs

                                         ;; no optimize compile (dev)
                                         ;;:optimizations :none
                                         ;; when no optimize uncomment
                                         ;;:output-dir "app/js/out"

                                         ;; simple compile (dev)
                                         :optimizations :simple

                                         ;; advanced compile (prod)
                                        ;:optimizations :advanced

                                        ;:source-map "app/js/test.js.map"
                                         :pretty-print true
                                         :output-wrapper true
                                         }}
                       :frontend {:id "dmedit-om"
                                  :figwheel true
                                  :source-paths ["src_front"]
                                  :incremental true
                                  :jar true
                                  :assert true
                                  :compiler {:output-to "app/js/front.js"
                                             :externs ["app/js/externs.js"]
                                             :warnings true
                                             :elide-asserts true
                                             ;; :target :nodejs

                                             ;; no optimize compile (dev)
                                             ;;:optimizations :none
                                             ;; when no optimize uncomment
                                             :output-dir "app/js/out"

                                             ;; simple compile (dev)
                                             ;;:optimizations :simple

                                             ;; advanced compile (prod)
                                             :optimizations :none

                                             ;;:source-map "app/js/test.js.map"
                                             :pretty-print true
                                             :output-wrapper true
                                             }}}}
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :figwheel {:http-server-root "public"
             :ring-handler figwheel-middleware/app
             :nrepl-port 7888
             :server-port 3449})
