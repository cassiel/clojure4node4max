(ns repl-test.core
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

(defn -main [& args]
  "I don't do a whole lot."
  (println "Hello, World!"))

(set! *main-cli-fn* -main)
