(defproject promises "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.10.339"]
                                  [org.clojure/core.match "1.0.0"]
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
