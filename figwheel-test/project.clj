(defproject figwheel-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.107"]]
  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.9"]]
  :clean-targets ^{:protect false} ["target"]
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src-cljs"]
                        :figwheel true
                        :compiler {:main net.cassiel.figwheel-test.core
                                   :output-to "main-dev.js"
                                   :target :nodejs
                                   :output-dir "target/dev"
                                   :optimizations :none
                                   :pretty-print true
                                   :parallel-build true}}
                       {:id "prod"
                        :source-paths ["src-cljs"]
                        :figwheel true
                        :compiler {:main net.cassiel.figwheel-test.core
                                   :output-to "main.js"
                                   :target :nodejs
                                   :output-dir "target/prod"
                                   :externs ["externs.js"]
                                   :optimizations :advanced
                                   :pretty-print true
                                   :parallel-build true}}]}
  :figwheel {})
