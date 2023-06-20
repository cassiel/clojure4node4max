(ns net.cassiel.osc.component.system
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [net.cassiel.osc.component.max-api :as max-api]
            [net.cassiel.osc.component.port :as port]
            [cljs.core.async :as async :refer [<! >! go go-loop]]
            [cljs.core.async.interop :refer [<p!]]
            [oops.core :refer [oget oget+ oset! oset!+ ocall]]))

;; This is the main component system which can be launched once we've
;; bootstrapped an initial system that talks to Max and (asynchronously)
;; retrieves configuration information from a Max dictionary.

(defrecord SYSTEM [max-api installed?]
  Object
  (toString [this] "SYSTEM")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action (let [S (atom nil)]
                        (fn []
                          (go (let [config (-> (<p! (ocall (:max-api max-api) :getDict "CONFIG" ))
                                               (js->clj :keywordize-keys true))]
                                (reset! S (-> (component/system-map :max-api max-api
                                                                    :port (port/map->PORT {:config config}))
                                              component/start))))
                          (assoc this :S S :installed? true)))))

  (stop [this]
    (stopping this
              :on installed?
              :action (fn []
                        (when-let [s (deref (:S this))]
                          (component/stop s))
                        (assoc this :S nil :installed? false))))  )
