(ns net.cassiel.blofeld.component.presets
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [net.cassiel.blofeld.component.channel-set :as channel-set]
            [net.cassiel.blofeld.component.max-api :as max-api]
            [net.cassiel.blofeld.async-tools :as async-tools]
            [cljs.core.async :refer [>!]]))

(defn handle-preset-recall
  "Preset recall into the 'fast' channel; gets throttled to the slow channel."
  [presets bank pgm]
  (go (>! (-> presets :channel-set :channels :preset-index-fast)
          [bank pgm])))

(defrecord PRESETS [max-api channel-set installed?]
  Object
  (toString [this] "PRESETS")

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(do
                         ;; Might as well install the throttler for preset changes:
                         (async-tools/throttle (-> channel-set :channels :preset-index-fast)
                                               (-> channel-set :channels :preset-index-slow))
                         (go-loop []
                           (when-let [v (<! (-> channel-set :channels :preset-index-slow))]
                             (println "RECALL" v)
                             (recur)))
                         (assoc this :installed? true))))

  (stop [this]
    (stopping this
              :on installed?
              :action #(assoc this :installed? false))))
