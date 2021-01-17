(ns user
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-promises.core :as p]
            [cljs-promises.async :as a :refer-macros [<?]]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.spec.alpha :as s]))

(def max-api (js/require "max-api"))

(a/extend-promises-as-pair-channels!)

(defn output-midi-seq [bytes]
  (go
    (loop [b (seq bytes)]
      (when-let [h (first b)]
        (<? (.outlet max-api h))
        (recur (rest b))))))

;; TODO: return value of output-midi-seq so that we can chain them.

;; Basic program change, channel 1:

(output-midi-seq [0xC0 0])

;;

(let [WALDORF   0x3E
      BLOFELD   0x13
      DEVICE-ID 0x7F
      SNDR-CMD  0x00
      BANK      0
      PROG      0
      CHK       0]
  (output-midi-seq [0xF0 WALDORF BLOFELD DEVICE-ID SNDR-CMD BANK PROG CHK 0xF7]))

(.-MESSAGE_TYPES.NUMBER max-api)

(def *STATE* (atom {}))

(let [number (.-MESSAGE_TYPES.NUMBER max-api)]
  (doto max-api
    (.removeHandlers number)
    ;; integers (numbers) in accumulate into *in-message* (backwards!):
    (.addHandler number (fn [i]
                          (swap! *STATE* update :incoming conj i)))))

(deref *STATE*)
