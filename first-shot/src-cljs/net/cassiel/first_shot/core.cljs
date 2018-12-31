(ns net.cassiel.first-shot.core
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

(def maxApi (js/require "max-api"))

(defn -main [& args]
  "I don't do a whole lot."
  (println "Hello World, at " (js/Date.))

  (-> maxApi
      (.addHandler "text" (fn [& args] (.outlet maxApi 1 2 3))))
  )

(set! *main-cli-fn* -main)
