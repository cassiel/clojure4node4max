(ns net.cassiel.osc.component.max-api
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]))

(defrecord MAX_API [installed?]
  Object
  (toString [this] "MAX_API")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(assoc this
                              :max-api (js/require "max-api")
                              :installed? true)))

  (stop [this]
    (stopping this
              :on installed?
              :action #(assoc this
                              :max-api nil
                              :installed? false))))
