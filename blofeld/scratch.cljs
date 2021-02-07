(ns user
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [net.cassiel.blofeld.manifest :as m]
            [net.cassiel.blofeld.incoming :as in]
            [net.cassiel.blofeld.midi :as midi]
            [net.cassiel.blofeld.sysex-in :as sysex-in]
            [net.cassiel.blofeld.async-tools :as tt]
            [net.cassiel.blofeld.core :as core]
            [clojure.core.match :refer [match]]
            [cljs-promises.core :as p]
            [cljs-promises.async :as a :refer-macros [<?]]
            [cljs.core.async :refer [put! chan <! close!]]))

(def max-api (js/require "max-api"))

(a/extend-promises-as-pair-channels!)

(def *STATE* (atom {}))

;; Channel pair for slowdown. message is [hi lo] for program change.
(def in-ch (chan))
(def out-ch (chan))

(let [number (.-MESSAGE_TYPES.NUMBER max-api)]
  (dorun (map #(.removeHandlers max-api %) [number "ctlin" "pgmin"]))
  (doto max-api
    (.addHandler number #(in/handle-byte *STATE* max-api %))
    (.addHandler "ctlin" #(in/handle-ctlin *STATE* %1 %2))
    (.addHandler "pgmin" #(in/handle-pgmin *STATE* in-ch %1))))

(tt/slowdown in-ch out-ch)

(go-loop []
  (when-let [[hi lo] (<! out-ch)]
    (println "Out of slowdown" [hi lo])
    ;; In fact, [127 0] would work here because we've just loaded the edit buffer with the patch:
    (midi/output-seq max-api [m/SOX m/WALDORF m/BLOFELD m/BROADCAST-ID m/SNDR hi lo 0 m/EOX])
    (recur)))


;; Test: basic program change, channel 1 (but doesn't switch bank):

(midi/output-seq max-api [0xC0 0])



(deref *STATE*)


(-> (deref *STATE*)
    :location)

;; --- Channel timer test

(close! in-ch)
(close! out-ch)

(js/require "max-api")


;; ----- COMPONENT

(reset! core/S (core/system))

(core/start)
(core/stop)
