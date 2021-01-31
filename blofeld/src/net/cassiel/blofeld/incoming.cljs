(ns net.cassiel.blofeld.incoming
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>!]]
            [cljs-promises.async :as a :refer-macros [<?]]
            [net.cassiel.blofeld.sysex-in :as sysex-in])
  )

(defn handle-byte
  "Handle a byte of sysex MIDI input."
  [*state* max-api i]
  ;; This flushing will output a sysex as [F0 nn nn nn ...] and as [F7] separately.
  ;; We don't flush except on message bytes, so this only works if we don't care
  ;; about getting the F7 straight away; all other messages will get delayed.
  (when (>= i 0x80)
    (when-let [msg (:partial-sysex (deref *state*))]
      (sysex-in/process *state* max-api (reverse msg))
      (swap! *state* dissoc :partial-sysex)))

  ;; Bytes accumulated backwards (hence `reverse` above):
  (swap! *state* update :partial-sysex conj i))

(defn handle-ctlin
  "handle control change. The only ones we care about are 0 and 32 for bank select.
   In fact we only have 8 banks so val will be 0 [A]..7 [H], for ctl as 0."
  [*state* val ctl]
  (when (= ctl 0)
    (swap! *state* assoc :bank val)))

(defn handle-pgmin
  "Handle program change, probably following a bank select: programs indexed from 1."
  [*state* ch pgm]
  (let [bank (or (-> *state* deref :bank) 0)
        p0 (dec pgm)]
    (swap! *state* assoc :program p0)
    (println "Got pgm " bank p0)
    (go (>! ch [bank p0]))))
