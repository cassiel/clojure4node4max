(ns net.cassiel.blofeld.sysex-in
  (:require [clojure.spec.alpha :as s]
            [net.cassiel.blofeld.manifest :as m]))

(s/def ::SYSEX-IN (s/or :SNDD #(= (take 5 %)
                                  [m/SOX m/WALDORF m/BLOFELD 0 m/SNDD])
                        :EOX #(= (first %) m/EOX)))

(defn process [max-api msg]
  ;;(.outlet max-api "print" (s/conform ::SYSEX-IN msg))
  (js/console.log (s/conform ::SYSEX-IN msg)))
