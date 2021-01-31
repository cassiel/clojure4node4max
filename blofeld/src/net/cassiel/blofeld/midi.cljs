(ns net.cassiel.blofeld.midi
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-promises.async :as a :refer-macros [<?]]))

;; TODO: return value of output-seq so that we can chain them.

(defn output-seq [max-api bytes]
  (go
    (loop [b (seq bytes)]
      (when-let [h (first b)]
        (<? (.outlet max-api h))
        (recur (rest b))))))
