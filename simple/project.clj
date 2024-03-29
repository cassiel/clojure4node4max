(defproject simple "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.11.60"]
                                  [com.bhauman/figwheel-main "0.2.18"]
                                  ;; optional but recommended
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]]
                   :resource-paths ["target"]
                   :clean-targets ^{:protect false} ["target"]}}
  :plugins [[com.github.liquidz/antq "RELEASE"]]
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]})
