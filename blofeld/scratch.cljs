(ns user
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [net.cassiel.blofeld.manifest :as m]
            [net.cassiel.blofeld.incoming :as in]
            [net.cassiel.blofeld.midi :as midi]
            [net.cassiel.blofeld.sysex-in :as sysex-in]
            [net.cassiel.blofeld.async-tools :as tt]
            [clojure.core.match :refer [match]]
            [cljs-promises.core :as p]
            [cljs-promises.async :as a :refer-macros [<?]]
            [cljs.core.async :refer [put! chan <! close!]]))

(def max-api (js/require "max-api"))

(a/extend-promises-as-pair-channels!)

(def *STATE* (atom {}))

(let [number (.-MESSAGE_TYPES.NUMBER max-api)]
  (doto max-api
    (.removeHandlers number)
    (.addHandler number #(in/handle-byte *STATE* max-api %))
    (.addHandler "ctlin" #(in/handle-ctlin *STATE* %1 %2))
    (.addHandler "pgmin" #(in/handle-pgmin *STATE* %1))))


;; Test: basic program change, channel 1 (but doesn't switch bank):

(midi/output-seq max-api [0xC0 0])

;; Test: patch request. [127 0] is the edit buffer.

(let [BANK 127
      PROG 0
      CHK  0]
  (midi/output-seq max-api [m/SOX m/WALDORF m/BLOFELD m/BROADCAST-ID m/SNDR BANK PROG CHK m/EOX]))

(deref *STATE*)

(let [filter-cutoff 78]
  (-> (deref *STATE*)
      :patch
      (nth filter-cutoff)))

(-> (deref *STATE*)
    :location)

;; --- Channel timer test

(def in-ch (chan))
(def out-ch (chan))

(go-loop []
  (when-let [v (<! out-ch)]
    (println "FLUSH" v)
    (recur)))

(tt/slowdown in-ch out-ch)

(go (>! in-ch (js/Date.)))

(close! in-ch)
(close! out-ch)
