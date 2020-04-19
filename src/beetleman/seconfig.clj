(ns beetleman.seconfig
  (:require [beetleman.seconfig.cli :as cli])
  (:gen-class))


(defn -main
  [& args]
  (cli/run args))
