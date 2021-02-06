(ns net.cassiel.blofeld.component.data
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [net.cassiel.blofeld.component.channel-set :as channel-set]
            [net.cassiel.blofeld.component.max-api :as max-api]
            [cljs.core.async :refer [>!]])  )

(defrecord DATA [max-api channel-set installed?]
  Object
  (toString [this] "DATA")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(assoc this installed? true)))

  (stop [this]
    (stopping this
              :on installed?
              :action #(assoc this installed? false))))
