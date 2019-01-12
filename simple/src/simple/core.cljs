(ns simple.core)

(def max-api (js/require "max-api"))

(let [msg (str "Started at " (js/Date.))]
  (doto max-api
    (.post msg)
    (.outlet msg)))
