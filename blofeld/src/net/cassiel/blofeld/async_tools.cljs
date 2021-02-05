(ns net.cassiel.blofeld.async-tools
  (:require-macros [cljs.core.async.macros :refer [go go-loop alt!]])
  (:require [cljs.core.async :as a :refer [>! <!]]))

(defn throttle
  "Speed limit messages coming into in-ch, echoing to out-chan after a timeout."
  [in-ch out-ch]
  (go-loop [held-value nil]
    (println "into slowdown with" held-value)
    (if held-value
      (alt!
        in-ch ([v] (when v (recur v)))
        (a/timeout 500) (do (>! out-ch held-value)
                            (recur nil)))
      (when-let [v (<! in-ch)] (recur v)))))
