(ns net.cassiel.blofeld.sysex-in
  (:require [clojure.spec.alpha :as s]
            [clojure.core.match :refer [match]]
            [net.cassiel.blofeld.manifest :as m]))

(s/def ::SNDD-LEN (s/coll-of number? :count 391))    ; includes SOX to SNDD, HI, LO, data and CHK.

(s/def ::PATCH (s/coll-of number? :count 383))

(s/def ::SNDD-HEAD #(let [SOX m/SOX
                          WALDORF m/WALDORF
                          BLOFELD m/BLOFELD
                          SNDD m/SNDD]
                      (match %
                             ([SOX WALDORF BLOFELD _ SNDD & _] :seq) :t :else nil)))

(s/def ::SNDD-TAIL #(let [data (->> %
                                    (drop 7)    ; drop including HI and LO.
                                    butlast)
                          sum (reduce + data)
                          chk (last %)]
                      (= (bit-and sum 0x7F) chk)))

(s/def ::SNDP-LEN (s/coll-of number? :count 9))         ; Excludes EOX

(s/def ::SNDP-HEAD #(let [SOX m/SOX
                          WALDORF m/WALDORF
                          BLOFELD m/BLOFELD
                          SNDP m/SNDP]
                      (match %
                             ([SOX WALDORF BLOFELD _ SNDP ll hh pp xx] :seq) :t :else nil)))

(s/def ::SYSEX-IN (s/or :SNDD (s/and ::SNDD-LEN ::SNDD-HEAD ::SNDD-TAIL)
                        :SNDP (s/and ::SNDP-LEN ::SNDP-HEAD)
                        :EOX #(= (first %) m/EOX)))

(defn handle-SNDD [msg]
  ;; Message runs from SOX, omits EOX. Checksum already validated.
  (let [[_ _ _ _ _ hi lo & rest] msg]
    [hi lo (butlast rest)]))

(defn process [*state* max-api msg]
  (match (s/conform ::SYSEX-IN msg)
         [:SNDD x] (let [[hi lo data] (handle-SNDD x)]
                     (swap! *state* assoc
                            :patch (s/conform ::PATCH data)
                            ;; Location is probably [127 0] for edit buffer.
                            :location [hi lo])
                     )
         [:SNDP x] (.outlet max-api "print" "SNDP [...]")
         [:EOX _] (.outlet max-api "print" "EOX")
         ::s/invalid (js/console.log (s/explain ::SYSEX-IN msg))
         :else (.outlet max-api "print" "(other)")))
