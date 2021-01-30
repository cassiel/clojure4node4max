(ns net.cassiel.blofeld.midi
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-promises.async :as a :refer-macros [<?]]))

;; TODO: return value of output-seq so that we can chain them.

(defn output-seq [max-api bytes]
  (go
    (loop [b (seq bytes)]
      (when-let [h (first b)]
        (<? (.outlet max-api h))
        (recur (rest b))))))

(def *IN-STATE* (atom {}))

(defn handle-byte
  "Handle a byte of MIDI input; send all complete messages to ``msg-chan``."
  [i msg-chan]
  ;; This flushing will output a sysex as [F0 nn nn nn ...] and as [F7] separately.
  ;; We don't flush except on message bytes, so this only works if we don't care
  ;; about getting the F7 straight away; all other messages will get delayed.
  (when (>= i 0x80)
    (when-let [msg (:message (deref *IN_STATE*))]
      (go (>! msg-chan (reverse msg)))
      (swap! *IN-STATE* dissoc :message)))

  ;; Bytes accumulated backwards (hence `reverse` above):
  (swap! *IN_STATE* update :message conj i))

(defn handle-ctlin
  "handle control change. The only ones we care about are 0 and 32 for bank select.
   In fact we only have 8 banks so val will be 0 [A]..7 [H], for ctl as 0."
  [val ctl *state*]
  (when (= ctl 0)
    (swap! *state* assoc :bank val)))

(defn handle-pgmin
  "Handle program change, probably following a bank select: programs indexed from 1."
  [pgm *state*]
  (swap! *state* assoc :program (dec pgm)))
