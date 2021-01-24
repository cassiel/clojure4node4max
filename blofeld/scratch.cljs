(ns user
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [net.cassiel.blofeld.manifest :as m]
            [net.cassiel.blofeld.midi :as midi]
            [net.cassiel.blofeld.sysex-in :as sysex-in]
            [cljs-promises.core :as p]
            [cljs-promises.async :as a :refer-macros [<?]]
            [cljs.core.async :refer [put! chan <! close!]]))

(def max-api (js/require "max-api"))

(a/extend-promises-as-pair-channels!)

;; Basic program change, channel 1:

(midi/output-seq max-api [0xC0 0])

(def msg-chan (chan))

(go-loop []
  (when-let [m (<! msg-chan)]
    (sysex-in/process max-api m)
    (recur)))

(let [number (.-MESSAGE_TYPES.NUMBER max-api)]
  (doto max-api
    (.removeHandlers number)
    (.addHandler number #(midi/handle-input % msg-chan))))

(let [BANK 0
      PROG 0
      CHK  0]
  (midi/output-seq max-api [m/SOX m/WALDORF m/BLOFELD m/BROADCAST-ID m/SNDR BANK PROG CHK m/EOX]))

(close! msg-chan)
