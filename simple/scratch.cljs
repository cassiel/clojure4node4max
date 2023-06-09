(ns user)

(js/Date.)

(-> (js/Intl.DateTimeFormat. "en-GB") (.format (js/Date.)))

(print (js/Date.))

(def max-api (js/require "max-api"))

(.post max-api "Hello World")

(doseq [x (range 10)]
  (.post max-api x))

(.outlet max-api "Hello World")

(.outlet max-api (js/Date.))

(.-MESSAGE_TYPES max-api)
