(ns net.cassiel.osc.component.port
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [cljs.core.async :as async :refer [<! >! go]]
            [oops.core :refer [oget oget+ oset! oset!+ ocall]]))

(defrecord PORT [config installed?]
  Object
  (toString [this] "PORT")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(let [_       (js/console.log "CONFIG" (clj->js config))
                             osc     (js/require "osc")
                             port    (new osc.UDPPort (clj->js (merge config {:localAddress  "0.0.0.0"
                                                                              :metadata      false})))
                             in-chan (async/chan)]
                         (doto port
                           (ocall :on "message" (fn [msg] (go (>! in-chan msg))))
                           (ocall :open))
                         (assoc this
                                :port port
                                :in-chan in-chan
                                :installed? true))))

  (stop [this]
    (stopping this
              :on installed?
              :action #(do (ocall (:port this) :close)
                           (async/close! (:in-chan this))
                           (assoc this
                                  :port nil
                                  :in-chan nil
                                  :installed? false)))))
