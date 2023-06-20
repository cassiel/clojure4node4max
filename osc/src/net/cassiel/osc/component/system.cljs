(ns net.cassiel.osc.component.system
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [net.cassiel.osc.component.max-api :as max-api]
            [net.cassiel.osc.component.port :as port]
            [net.cassiel.osc.component.process :as process]
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
              :action (let [;; Atom for the inner system:
                            S (atom nil)]
                        (fn []
                          (go (let [;; Asynchronous call out to Max to get the configuration dictionary:
                                    config (-> (<p! (ocall (:max-api max-api) :getDict "CONFIG"))
                                               (js->clj :keywordize-keys true))]
                                ;; Once that's in, we can pass it in as argument as we build the inner system,
                                ;; which we immediately start:
                                (reset! S (-> (component/system-map :max-api max-api
                                                                    :port (port/map->PORT {:config config})
                                                                    :process (component/using (process/map->PROCESS {})
                                                                                              [:port :max-api]))
                                              component/start))))
                          (assoc this :S S :installed? true)))))

  (stop [this]
    (stopping this
              :on installed?
              :action (fn []
                        ;; Slight race here if we stop really quickly before the async config fetch:
                        (when-let [s (deref (:S this))]
                          (component/stop s))
                        (assoc this :S nil :installed? false)))))
