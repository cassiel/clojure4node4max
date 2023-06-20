(ns net.cassiel.osc.component.process
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [cljs.core.async :as async :refer [<! >! go go-loop]]
            [oops.core :refer [oget oget+ oset! oset!+ ocall]]))

(defrecord PROCESS [port installed?]
  Object
  (toString [this] "PROCESS")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(do
                         (let [ch (:in-chan port)
                               osc-port (:port port)]
                           (go-loop []
                             (when-let [v (<! ch)]
                               (println "PROCESS" v)
                               (ocall osc-port :send v)
                               (recur))))
                         (assoc this :installed? true))))

  (stop [this]
    (stopping this
              :on installed?
              :action #(assoc this :installed? false))))
