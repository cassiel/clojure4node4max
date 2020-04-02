;; Mapping functions through (x, y).

(ns user
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-promises.core :as p]
            [cljs-promises.async :as a :refer-macros [<?]]
            [cljs.core.async :refer [put! chan <!]]))

(def max-api (js/require "max-api"))

(a/extend-promises-as-pair-channels!)

(.outlet max-api "m" "clear")
(.outlet max-api "m" "setcell" 0 0 "val" 1.0 1.0 1.0)
(.outlet max-api "m" "bang")

(def MATRIX-SIZE 16)

;; "go" for async:
(go (.outlet max-api "m" "clear")
    (doseq [x (range MATRIX-SIZE)]
      (<? (.outlet max-api "m" "setcell" x 0 "val" (rand) (rand) 0.0)))
    (.outlet max-api "m" "bang"))

(defn unbool [v]
  (if (boolean? v)
    (if v 1.0 0.0)
    v))

(defn render [f]
  (go (.outlet max-api "m" "clear")
      (doseq [x (range MATRIX-SIZE)
              y (range MATRIX-SIZE)]
        (let [v (f x y)
              [r g b] (map unbool (if (seqable? v) v [v v v]))]
          (<? (.outlet max-api "m" "setcell" x y "val" r g b))))
      (.outlet max-api "m" "bang")))

(render (fn [x y] [(= x 0) 0 0]))
