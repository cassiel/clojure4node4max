;; Play with promises:

(ns user
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-promises.core :as p]
            [cljs-promises.async :as a :refer-macros [<?]]
            [cljs.core.async :refer [put! chan <!]]))

(def max-api (js/require "max-api"))

(a/extend-promises-as-pair-channels!)



(go
  (let [pp (-> max-api (.getDict "X"))]
    (try
      (.post max-api (<? pp))
      (catch js/Error e
        (.post max-api (str "Error: " (ex-message e)))))
    ))

(.outlet max-api "show")


(go
  (let [pp (<? (-> max-api (.getDict "X")))
        pp' (-> pp
                js->clj
                (assoc :A (range 5)
                       :H "HELLO"
                       :W "WORLD")
                (update :A (partial map inc))
                clj->js)]
    (try
      (.post max-api pp)
      (.post max-api pp')
      (.setDict max-api "X" pp')
      (catch js/Error e
        (.post max-api (str "Error: " (ex-message e)))))
    ))


(clj->js {:A "HELLO"})

(-> {:A [1 2 3]}
    (update :A (partial map inc)))
