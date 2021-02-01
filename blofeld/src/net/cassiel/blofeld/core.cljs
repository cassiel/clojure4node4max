(ns net.cassiel.blofeld.core
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.blofeld.component.max-api :as max-api]
            [net.cassiel.blofeld.component.channel-set :as channel-set]))

(defn system []
  (component/system-map :max-api (max-api/map->MAX_API {})
                        :channel-set (channel-set/map->CHANNEL_SET {})))

(defonce S (atom (system)))

(defn start []
  (swap! S component/start))

(defn stop []
  (swap! S component/stop))

#_ (reset! S (system))
