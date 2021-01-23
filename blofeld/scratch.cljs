(ns user
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [net.cassiel.blofeld.midi :as midi]
            [cljs-promises.core :as p]
            [cljs-promises.async :as a :refer-macros [<?]]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.spec.alpha :as s]))

(def max-api (js/require "max-api"))

(a/extend-promises-as-pair-channels!)

;; Basic program change, channel 1:

(midi/output-seq max-api [0xC0 0])

(def msg-chan (chan))

(go-loop []
  (let [m (<! msg-chan)]
    (println (first m) (count m))
    (recur)))

(let [number (.-MESSAGE_TYPES.NUMBER max-api)]
  (doto max-api
    (.removeHandlers number)
    ;; integers (numbers) in accumulate into *in-message* (backwards!):
    #_ (.addHandler number (fn [i]
                             (swap! *STATE* update :incoming conj i)))
    (.addHandler number #(midi/handle-input % msg-chan))))

(let [WALDORF   0x3E
      BLOFELD   0x13
      DEVICE-ID 0x7F
      SNDR-CMD  0x00
      BANK      0
      PROG      0
      CHK       0]
  (midi/output-seq max-api [0xF0 WALDORF BLOFELD DEVICE-ID SNDR-CMD BANK PROG CHK 0xF7]))
