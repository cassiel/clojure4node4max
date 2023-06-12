(ns net.cassiel.osc.core
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.osc.component.port :as port]
            [net.cassiel.osc.component.max-api :as max-api]
            ))

(defn system []
  (component/system-map :max-api (max-api/map->MAX_API {})
                        :port (port/map->PORT {})))

(defonce S (atom (system)))

(defn start []
  (swap! S component/start))

(defn stop []
  (swap! S component/stop))
