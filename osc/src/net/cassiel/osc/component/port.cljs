(ns net.cassiel.osc.component.port
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [cljs.core.async :as async :refer [<! >! go]]
            [oops.core :refer [oget oget+ oset! oset!+ ocall]]))

(defrecord PORT [installed?]
  Object
  (toString [this] "PORT")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(let [osc     (js/require "osc")
                             in-port (new osc.UDPPort #js {:localAddress  "0.0.0.0"
                                                           :localPort     54321
                                                           :remoteAddress "127.0.0.1"
                                                           :remotePort    54322
                                                           :metadata      true})
                             _ (js/console.log "CHECK" (oget in-port :options))
                             in-chan (async/chan)]
                         (doto in-port
                           (ocall :on "message" (fn [msg] (go (>! in-chan msg))))
                           (ocall :open))
                         (assoc this
                                :in-port in-port
                                :in-chan in-chan
                                :installed? true))))

  (stop [this]
    (stopping this
              :on installed?
              :action #(do (ocall (:in-port this) :close)
                           (async/close! (:in-chan this))
                           (assoc this
                                  :in-port nil
                                  :in-chan nil
                                  :installed? false)))))
