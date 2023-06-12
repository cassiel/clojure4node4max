;; Play with OSC:

(ns user
  (:require [cljs.core.async :as async :refer [put! chan <! go]]
            [cljs.core.async.interop :refer [<p!]]))

(def max-api (js/require "max-api"))

(def osc (js/require "osc"))

(def port (new osc.UDPPort #js {:localAddress "0.0.0.0"
                                :localPort    54321
                                :metadata     true}))
(.close port)
