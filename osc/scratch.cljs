;; Play with OSC:

(ns user
  (:require [cljs.core.async :as async :refer [<! >! go go-loop]]
            [cljs.core.async.interop :refer [<p!]]
            [net.cassiel.osc.core :as core]
            [oops.core :refer [oget oget+ oset! oset!+ ocall]]))

(def max-api (js/require "max-api"))

(def osc (js/require "osc"))

(def port (new osc.UDPPort #js {:localAddress "0.0.0.0"
                                :localPort    54321
                                :metadata true}))
(ocall port :close)

;; ----- COMPONENT (OBSOLETE)

(reset! core/S (core/system))

(core/start)
(core/stop)

;; ----- New, more complex bootstrap:

(def B (core/bootstrap))

(:S B)

((:start B))
((:stop B))


;; --- Attempts at configuration:

(let [max-api (js/require "max-api")]
  (go (-> (<p! (ocall max-api :getDict "CONFIG"))
          (js->clj :keywordize-keys true)
          :remoteAddress
          js/console.log))
  )

(-> core/S deref :port :X)

(defrecord FOO [a b c])

(FOO. 2 3 4)
(map->FOO {:a 12})

;; --- PORT

(let [ch (-> (:S B) deref :port :in-chan)]
  (go-loop []
    (when-let [v (<! ch)]
      (js/console.log v)
      (recur)))
  )

(let [port (-> core/S deref :port :port)]
  (ocall port :send (clj->js {:address "/1/fader1"
                              :args [{:type "f" :value (rand)}]})))

#js {:address "/sending" :args [{:type "f" :value (rand)}]}

;; --- DICTIONARY TESTS

(defn update-count [key]
  (go (let [d (-> (<p! (ocall max-api :getDict "X"))
                  (js->clj :keywordize-keys true))
            n (inc (get d key))]
        (<p! (ocall max-api :updateDict "X" (name key) n))
        (ocall max-api :outlet "show"))))
