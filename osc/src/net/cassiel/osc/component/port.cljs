(ns net.cassiel.osc.component.port
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [oops.core :refer [oget oget+ oset! oset!+ ocall]]))

(defrecord PORT [installed?]
  Object
  (toString [this] "PORT")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(let [osc (js/require "osc")
                             port (new osc.UDPPort #js {:localAddress "0.0.0.0"
                                                        :localPort    54321
                                                        :metadata     true})]
                         (ocall port :open) ; NOPE: have to set up message listeners first. TODO: async channel.
                         (assoc this
                                :port port
                                :installed? true))))

  (stop [this]
    (stopping this
              :on installed?
              :action #(do (ocall (:port this) :close)
                           (assoc this
                                  :port nil
                                  :installed? false)))))
