(ns net.cassiel.blofeld.core
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.blofeld.component.max-api :as max-api]
            [net.cassiel.blofeld.component.handlers :as handlers]
            [net.cassiel.blofeld.component.channel-set :as channel-set]
            [net.cassiel.blofeld.component.data :as data]
            ))

(defn system []
  (component/system-map :channel-set (channel-set/map->CHANNEL_SET {})
                        :max-api (max-api/map->MAX_API {})
                        :data (data/map->DATA {})
                        :handlers (component/using (handlers/map->HANDLERS {})
                                                   [:max-api :channel-set])))

(defonce S (atom (system)))

(defn start []
  (swap! S component/start))

(defn stop []
  (swap! S component/stop))

#_ (reset! S (system))
