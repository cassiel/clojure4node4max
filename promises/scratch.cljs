;; Play with promises:

(ns user
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-promises.core :as p]
            [cljs-promises.async :as a :refer-macros [<?]]
            [cljs.core.async :refer [put! chan <!]]))

(a/extend-promises-as-pair-channels!)

(go
  (let [pp (-> max-api (.getDict "X"))]
    (try
      (.post max-api (<? pp))
      (catch js/Error e
        (.post max-api (str "Error: " (ex-message e)))))
    ))
