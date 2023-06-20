(ns net.cassiel.osc.component.process
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [cljs.core.async :as async :refer [<! >! go go-loop]]
            [cljs.core.async.interop :refer [<p!]]
            [oops.core :refer [oget oget+ oset! oset!+ ocall]]))

(defrecord PROCESS [port max-api installed?]
  Object
  (toString [this] "PROCESS")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(do
                         (let [ch (:in-chan port)
                               osc-port (:port port)
                               max-api (:max-api max-api)]
                           (go-loop []
                             (when-let [v (<! ch)]
                               ;; (println "PROCESS" v)
                               (ocall osc-port :send v)

                               (let [address (oget v :address)
                                     args (-> v
                                              (oget :args)
                                              js->clj
                                              seq
                                              (conj 9999)
                                              clj->js)]
                                 (go
                                   (<p! (ocall max-api :updateDict "DISPLAY" address args))
                                   (ocall max-api :outlet "show")))
                               (recur))))
                         (assoc this :installed? true))))

  (stop [this]
    (stopping this
              :on installed?
              :action #(assoc this :installed? false))))
