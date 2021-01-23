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

(defn handle-input
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
