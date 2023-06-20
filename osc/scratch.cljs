;; Play with OSC:

(ns user
  (:require [cljs.core.async :as async :refer [<! >! go go-loop]]
            [cljs.core.async.interop :refer [<p!]]
            [net.cassiel.osc.core :as core]
            [oops.core :refer [oget oget+ oset! oset!+ ocall]]))

;; ----- (OUTER) COMPONENT:

(reset! core/BOOT (core/boot-system))

(core/start)
(core/stop)

(-> core/BOOT deref :system :S deref :port :port (oget :options))

;; --- PORT

(let [port-component (-> core/BOOT deref :system :S deref :port)
      ch (:in-chan port-component)
      osc-port (:port port-component)]
  (go-loop []
    (when-let [v (<! ch)]
      (println v)
      (ocall osc-port :send v)
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

(clj->js (conj (seq [1 2 3]) 99))

(update {} :X inc)
(update {} "X" inc)
