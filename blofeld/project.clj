(defproject promises "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.2"]]
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.10.758"]
                                  [org.clojure/core.async "1.3.610"]
                                  [org.clojure/core.match "1.0.0"]
                                  [com.stuartsierra/component "1.0.0"]
                                  [net.cassiel/lifecycle "0.1.0-SNAPSHOT"]
                                  [com.bhauman/figwheel-main "0.2.3"]
                                  ;; optional but recommended
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]
                                  ;; NICK: add support for resolving Javascript promises:
                                  [jamesmacaulay/cljs-promises "0.1.0"]
                                  ;; Other useful stuff:
                                  [binaryage/oops "0.6.4"]]
                   :resource-paths ["target"]
                   :clean-targets ^{:protect false} ["target"]}}
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]})
