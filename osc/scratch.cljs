;; Play with OSC:

(ns user
  (:require [cljs.core.async :as async :refer [put! chan <! go]]
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

(let [p (-> core/S deref :port :port)]
  (ocall p :on "message" #(console.log "MESSAGE" %))
  (ocall p :open))
