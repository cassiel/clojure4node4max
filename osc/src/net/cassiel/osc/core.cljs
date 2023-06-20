(ns net.cassiel.osc.core
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.osc.component.system :as system]
            [net.cassiel.osc.component.max-api :as max-api]))

;; This is a rather messy bootstrap. We need to bring up max-api and do a
;; promise-based fetch from a Max dictionary to get our configuration, before
;; we can actually bring up the whole system.

(defn boot-system []
  (component/system-map :max-api (max-api/map->MAX_API {})
                        :system (component/using (system/map->SYSTEM {})
                                                 [:max-api])))

(defonce BOOT (atom (boot-system)))

(defn start []
  (swap! BOOT component/start))

(defn stop []
  (swap! BOOT component/stop))

(start)
