(ns net.cassiel.blofeld.sysex-in
  (:require [clojure.spec.alpha :as s]
            [clojure.core.match :refer [match]]
            [net.cassiel.blofeld.manifest :as m]))

(s/def ::SYSEX-IN (s/or :SNDD #(= (take 5 %)
                                  [m/SOX m/WALDORF m/BLOFELD 99 m/SNDD])
                        :EOX #(= (first %) m/EOX)))

(def *TEMP* (atom nil))

(defn process [max-api msg]
  ;;(.outlet max-api "print" (s/conform ::SYSEX-IN msg))
  (let [v (s/conform ::SYSEX-IN msg)]
    (reset! *TEMP* v)
    (js/console.log v)))

(deref *TEMP*)
