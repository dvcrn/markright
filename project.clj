(defproject markright "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/dvcrn/markright/"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"
                  :exclusions [org.apache.ant/ant]]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om "1.0.0-alpha7"]]

  :source-paths ["src/cljs"
                 "src/clj"]

  :profiles {:dev {:source-paths ["src/dev"]
                   :dependencies [[thheller/shadow-devtools "0.1.30"]]}}
  )
