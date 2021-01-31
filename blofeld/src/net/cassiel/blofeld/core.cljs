(ns net.cassiel.blofeld.core
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.blofeld.components.max-api :as max-api]))

(defn system []
  (component/system-map :max-api (max-api/map->MAX_API {})))

(defonce S (atom (system)))

(defn start []
  (swap! S component/start))

(defn stop []
  (swap! S component/stop))

#_ (reset! S (system))
