(ns net.cassiel.blofeld.sysex-in
  (:require [clojure.spec.alpha :as s]
            [clojure.core.match :refer [match]]
            [net.cassiel.blofeld.manifest :as m]))

(s/def ::SNDD-LEN (s/coll-of number? :count 391))    ; includes SOX to SNDD, HI, LO, data and CHK.

(s/def ::SNDD-HEAD #(let [SOX m/SOX
                            WALDORF m/WALDORF
                            BLOFELD m/BLOFELD
                            SNDD m/SNDD]
                        (match [%]
                               [([SOX WALDORF BLOFELD _ SNDD & _] :seq)] :t :else nil)))

(s/def ::SNDD-TAIL #(let [data (->> %
                                    (drop 7)    ; drop including HI and LO.
                                    butlast)
                          sum (reduce + data)
                          chk (last %)]
                      (= (bit-and sum 0x7F) chk)))

(s/def ::SYSEX-IN (s/or :SNDD (s/and ::SNDD-LEN ::SNDD-HEAD ::SNDD-TAIL)
                        :EOX #(= (first %) m/EOX)))

(defn process [max-api msg]
  (match (s/conform ::SYSEX-IN msg)
         [:SNDD x] (.outlet max-api "print" "SNDD" (count x))
         [:EOX _] (.outlet max-api "print" "EOX")
         ::s/invalid (js/console.log (s/explain ::SYSEX-IN msg))
         :else (.outlet max-api "print" "(other)")))
