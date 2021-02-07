(ns net.cassiel.blofeld.component.handlers
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [net.cassiel.blofeld.component.presets :as presets]
            [net.cassiel.blofeld.async-tools :as async-tools]
            [cljs.core.async :refer [>!]]))

(def STATE (atom nil))

(defn handle-byte
  "Handle a byte of sysex MIDI input."
  [presets i]
  ;; This flushing will output a sysex as [F0 nn nn nn ...] and as [F7] separately.
  ;; We don't flush except on message bytes, so this only works if we don't care
  ;; about getting the F7 straight away; all other messages will get delayed.
  (when (>= i 0x80)
    (when-let [msg (:partial-sysex (deref STATE))]
      (presets/handle-sysex presets (reverse msg))
      (swap! STATE dissoc :partial-sysex)))

  ;; Bytes accumulated backwards (hence `reverse` above):
  (swap! STATE update :partial-sysex conj i))

(defn handle-ctlin
  "handle control change. The only ones we care about are 0 and 32 for bank select.
   In fact we only have 8 banks so val will be 0 [A]..7 [H], for ctl as 0."
  [val ctl]
  (println "Got ctl " val ctl)
  (when (= ctl 0)
    (swap! STATE assoc :bank val)))

(defn handle-pgmin
  "Handle program change, probably following a bank select: programs indexed from 1."
  [presets pgm]
  (let [bank (or (-> STATE deref :bank) 0)
        p0 (dec pgm)]
    (swap! STATE assoc :program p0)
    (println "Got pgm " bank p0)
    ;; FIX: call into data component instead.
    (presets/handle-preset-recall presets bank pgm)))

(defrecord HANDLERS [max-api presets installed?]
  Object
  (toString [this] "HANDLERS")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action (fn [] (let [max-api (:max-api max-api)
                                   number (.-MESSAGE_TYPES.NUMBER max-api)]
                               (reset! STATE nil)
                               (doto max-api
                                 (.addHandler number (partial handle-byte max-api))
                                 (.addHandler "ctlin" handle-ctlin)
                                 (.addHandler "pgmin" (partial handle-pgmin presets)))
                               (assoc this
                                      :installed? true)))))

  (stop [this]
    (stopping this
              :on installed?
              :action (fn [] (let [max-api (:max-api max-api)
                                   number (.-MESSAGE_TYPES.NUMBER max-api)]
                               (do (dorun (map #(.removeHandlers max-api %) [number "ctlin" "pgmin"]))
                                   (assoc this
                                          :installed? false)))))))
