(defproject markright "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/dvcrn/markright/"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145" :exclusions [org.apache.ant/ant]]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om "1.0.0-alpha8"]
                 [com.cemerick/piggieback "0.2.1"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [figwheel "0.4.0"]]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-externs "0.1.3"]
            [lein-figwheel "0.4.0" :exclusions [org.clojure/core.cache]]]
  :source-paths ["src_tools"]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {
              :builds [ 
                       {:id "main:prod"
                        :source-paths ["src"]
                        :incremental true
                        :jar true
                        :assert true
                        :compiler {:output-to "app/js/main.js"
                                   :externs ["app/js/externs.js"
                                             "node_modules/closurecompiler-externs/path.js"
                                             "node_modules/closurecompiler-externs/process.js"]
                                   :warnings true
                                   :elide-asserts true
                                   :target :nodejs

                                   :optimizations :simple
                                   }}

                       {:id "main:dev"
                        :figwheel true
                        :source-paths ["src"]
                        :incremental true
                        :jar true
                        :assert true
                        :compiler {:main markright.core
                                   :output-to "app/js/main.js"
                                   :externs ["app/js/externs.js"
                                             "node_modules/closurecompiler-externs/path.js"
                                             "node_modules/closurecompiler-externs/process.js"]
                                   :warnings true
                                   :elide-asserts true
                                   :target :nodejs

                                   :output-dir "app/js/out-main/"
                                   :optimizations :none
                                   :pretty-print true
                                   :output-wrapper true
                                   }}

                       {:id "actions:prod"
                        :source-paths ["src_actions"]
                        :incremental true
                        :jar true
                        :assert true
                        :compiler {:output-to "app/js/actions.js"
                                   :externs ["app/js/externs.js"
                                             "node_modules/closurecompiler-externs/path.js"
                                             "node_modules/closurecompiler-externs/process.js"]
                                   :warnings true
                                   :elide-asserts true
                                   :target :nodejs

                                   :optimizations :simple
                                   }}

                       {:id "actions:dev"
                        :figwheel true
                        :source-paths ["src_actions"]
                        :incremental true
                        :jar true
                        :assert true
                        :compiler {:main markright.core
                                   :output-to "app/js/actions.js"
                                   :externs ["app/js/externs.js"
                                             "node_modules/closurecompiler-externs/path.js"
                                             "node_modules/closurecompiler-externs/process.js"]
                                   :warnings true
                                   :elide-asserts true
                                   :target :nodejs

                                   ;; without optimizations is currently not working
                                   ;; we need 1 file containing the export directly
                                   ;;:output-dir "app/js/out-actions"
                                   :optimizations :simple
                                   :pretty-print true
                                   :output-wrapper true
                                   }}

                       {:id "frontend:prod"
                        :source-paths ["src_front"]
                        :incremental true
                        :jar true
                        :assert true
                        :compiler {:output-to "app/js/front.js"
                                   :externs ["app/externs.js"]
                                   :warnings true
                                   :elide-asserts true

                                   :optimizations :simple
                                   }}

                       {:id "frontend:dev"
                        :figwheel true
                        :source-paths ["src_front" "src_dev"]
                        :incremental true
                        :jar true
                        :assert true
                        :compiler {:main "markright.core"
                                   :output-to "app/js/front.js"
                                   :externs ["app/externs.js"]
                                   :warnings true
                                   :elide-asserts true

                                   :output-dir "app/js/out-frontend"
                                   :asset-path "js/out-frontend"
                                   :optimizations :none
                                   :pretty-print true
                                   :output-wrapper true
                                   }}
                       ]}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :figwheel {:http-server-root "public"
             :ring-handler figwheel-middleware/app
             :nrepl-port 7888
             :server-port 3449})
