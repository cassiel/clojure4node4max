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
              :action #(let [preset-index-fast (a/chan)
                             preset-index-slow (a/chan)]
                         (assoc this
                                :channels {:preset-index-fast preset-index-fast
                                           :preset-index-slow preset-index-slow}
                                :installed? true))))

  (stop [this]
    (stopping this
              :on installed?
              :action #(do (dorun (map a/close! (vals channels)))
                           (assoc this
                                  :channels nil
                                  :installed? false)))))
