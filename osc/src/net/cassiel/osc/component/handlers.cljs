(ns net.cassiel.osc.component.handlers
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.osc.component.max-api :as max-api]
            [net.cassiel.lifecycle :refer [starting stopping]]))

(defrecord HANDLERS [max-api installed?]
  Object
  (toString [this] "HANDLERS")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(assoc this :installed true)))

  (stop [this]
    (stopping this
              :on installed?
              :action #(assoc this :installed false))))
