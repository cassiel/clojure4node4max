;; Play with OSC:

(ns user
  (:require [cljs.core.async :as async :refer [<! >! go go-loop]]
            [cljs.core.async.interop :refer [<p!]]
            [net.cassiel.osc.core :as core]
            [oops.core :refer [oget oget+ oset! oset!+ ocall]]))

(def max-api (js/require "max-api"))

(def osc (js/require "osc"))

(def port (new osc.UDPPort #js {:localAddress "0.0.0.0"
                                :localPort    54321
                                :metadata true}))
(ocall port :close)

;; ----- COMPONENT

(reset! core/S (core/system))

(core/start)
(core/stop)

;; --- PORT



(let [ch (-> core/S deref :port :in-chan)]
  (go-loop []
    (when-let [v (<! ch)]
      (js/console.log v)
      (recur)))
  )

(let [port (-> core/S deref :port :port)]
  (ocall port :send (clj->js {:address "/1/fader1"
                              :args [{:type "f" :value (rand)}]})))

#js {:address "/sending" :args [{:type "f" :value (rand)}]}

;; ---
