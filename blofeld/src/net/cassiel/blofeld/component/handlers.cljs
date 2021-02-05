(ns net.cassiel.blofeld.component.handlers
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]))

(defrecord HANDLERS [max-api installed?]
  Object
  (toString [this] "HANDLERS")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action (fn [] (let [max-api (:max-api max-api)
                                   number (.-MESSAGE_TYPES.NUMBER max-api)]
                               (do (doto max-api
                                     (.addHandler number #(println "NUMBER" %)))
                                   (assoc this
                                          :installed? true))))))

  (stop [this]
    (stopping this
              :on installed?
              :action (fn [] (let [max-api (:max-api max-api)
                                   number (.-MESSAGE_TYPES.NUMBER max-api)]
                               (do (dorun (map #(.removeHandlers max-api %) [number]))
                                   (assoc this
                                          :installed? false)))))))
