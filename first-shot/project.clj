(defproject repl_test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [org.clojure/clojurescript "1.9.521"]]
  :plugins [[lein-cljsbuild "1.1.5"]]
  :cljsbuild {:builds [{:id "prod"
                        :source-paths ["src-cljs"]
                        :compiler {:main net.cassiel.first-shot.core
                                   :output-to "package/index.js"
                                   :target :nodejs
                                   :output-dir "target"
                                   ;; :externs ["externs.js"]
                                   :optimizations :advanced
                                   :pretty-print true
                                   :parallel-build true}}]})
