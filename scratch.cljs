(ns user)

(js/Date.)

(print (js/Date.))

(def max-api (js/require "max-api"))

(.post max-api "Hello World")

(doseq [x (range 10)]
  (.post max-api x))

(.outlet max-api "Hello World")

(.outlet max-api (js/Date.))

(.-MESSAGE_TYPES max-api)

;; Play with promises:

(ns user
  (:require [cljs-promises.core :as p]))

(cljs-promises.async/extend-promises-as-pair-channels!)

(-> max-api (.getDict "X"))
