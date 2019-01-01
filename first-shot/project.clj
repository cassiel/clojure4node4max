(defproject first-shot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.439"]]
  :plugins [[lein-cljsbuild "1.1.7"]]
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src-cljs"]
                        :compiler {:main net.cassiel.first-shot.core
                                   :output-to "./main-dev.js"
                                   :target :nodejs
                                   :output-dir "target/dev"
                                   :optimizations :none
                                   :pretty-print true
                                   :parallel-build true}}
                       {:id "prod"
                        :source-paths ["src-cljs"]
                        :compiler {:main net.cassiel.first-shot.core
                                   :output-to "./main.js"
                                   :target :nodejs
                                   :output-dir "target/prod"
                                   :externs ["externs.js"]
                                   :optimizations :advanced
                                   :pretty-print true
                                   :parallel-build true}}]})
