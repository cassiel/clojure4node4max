(ns user
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [net.cassiel.blofeld.manifest :as m]
            [net.cassiel.blofeld.midi :as midi]
            [net.cassiel.blofeld.sysex-in :as sysex-in]
            [clojure.core.match :refer [match]]
            [cljs-promises.core :as p]
            [cljs-promises.async :as a :refer-macros [<?]]
            [cljs.core.async :refer [put! chan <! close!]]))

(def max-api (js/require "max-api"))

(a/extend-promises-as-pair-channels!)

(def *STATE* (atom {}))

(def msg-chan (chan))

(let [number (.-MESSAGE_TYPES.NUMBER max-api)]
  (doto max-api
    (.removeHandlers number)
    (.addHandler number #(midi/handle-byte % msg-chan))
    (.addHandler "ctlin" #(midi/handle-ctlin %1 %2 *STATE*))
    (.addHandler "pgmin" #(midi/handle-pgmin %1 *STATE*))))

(go-loop []
  (when-let [m (<! msg-chan)]
    (sysex-in/process *STATE* max-api m)
    (recur)))

;; Test: basic program change, channel 1:

(midi/output-seq max-api [0xC0 0])

;; Test: patch request. [127 0] is the edit buffer.

(let [BANK 127
      PROG 0
      CHK  0]
  (midi/output-seq max-api [m/SOX m/WALDORF m/BLOFELD m/BROADCAST-ID m/SNDR BANK PROG CHK m/EOX]))

(close! msg-chan)
(deref *STATE*)

(let [filter-cutoff 78]
  (-> (deref *STATE*)
      :patch
      (nth filter-cutoff)))

(-> (deref *STATE*)
    :location)
