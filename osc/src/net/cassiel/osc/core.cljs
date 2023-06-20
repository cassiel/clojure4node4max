(ns net.cassiel.osc.core
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.osc.component.system :as system]
            [net.cassiel.osc.component.port :as port]
            [net.cassiel.osc.component.max-api :as max-api]
            [cljs.core.async :as async :refer [<! >! go go-loop]]
            [cljs.core.async.interop :refer [<p!]]
            [oops.core :refer [oget oget+ oset! oset!+ ocall]]))

;; This is a rather messy bootstrap. We need to bring up max-api and do a
;; promise-based fetch from a Max dictionary to get our configuration, before
;; we can actually bring up the whole system.

;; TODO: this feels like a defrecord with first-class start/stop functions.

(defn boot-system []
  (component/system-map :max-api (max-api/map->MAX_API {})
                        :system (component/using (system/map->SYSTEM {})
                                                 [:max-api])))

(defonce BOOT (atom (boot-system)))

(defn start []
  (swap! BOOT component/start))

(defn stop []
  (swap! BOOT component/stop))
