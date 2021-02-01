(ns net.cassiel.blofeld.component.channel-set
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [cljs.core.async :as a]))

(defrecord CHANNEL_SET [channels installed?]
  Object
  (toString [this] "CHANNEL_SET")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(assoc this
                              :channels {:A (a/chan)
                                         :B (a/chan)
                                         :C (a/chan)}
                              :installed? true)))

  (stop [this]
    (stopping this
              :on installed?
              :action #(do (dorun (map a/close! (vals channels)))
                           (assoc this
                                  :channels nil
                                  :installed? false)))))
