(ns beetleman.seconfig.cli
  (:require [beetleman.seconfig.core :as seconfig]
            [cli-matic.core :refer [run-cmd]]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]))


(defn encrypt
  [{:keys [] :as opt}]
  (println "TODO"))


(defn decrypt
  [{:keys [] :as opt}]
  (println "TODO"))


(defn keygen
  [{:keys [o]}]
  (with-open [out (io/writer o)]
    (pprint/pprint {:user (seconfig/keypair!)
                    :ci   (seconfig/keypair!)}
                   out)))


(def CONFIGURATION
  {:app      {:command     "seconfig"
              :description
              "A command-line tool for storing secrets in VCS in safe way"
              :version     "0.0.1"}

   :commands
   [{:command     "encrypt"
     :description "Encrypt config"
     :opts        [{:option "i" :as "Input path" :type :ednfile}
                   {:option "user-sk"
                    :env    "USER_SK"
                    :as     "User secret key"
                    :type   :string}
                   {:option "ci-pk"
                    :evn    "CI_PK"
                    :as     "CI publick key"
                    :type   :string}]
     :runs        encrypt}
   
    {:command     "decrypt"
     :description "Decrypt config"
     :opts        [{:option "i" :as "Input path" :type :ednfile}
                   {:option "user-pk"
                    :env    "USER_PK"
                    :as     "User public key"
                    :type   :string}
                   {:option "ci-sk"
                    :env    "CI_SK"
                    :as     "CI secret key"
                    :type   :string}]
     :runs        decrypt}
   
    {:command     "keygen"
     :description "Generate new keys. Do not store it in VCS!"
     :opts        [{:option  "o"
                    :as      "Keys file path"
                    :type    :string
                    :default "seconfig.keys.edn"}]
     :runs        keygen}]})


(defn run
  [args]
  (run-cmd args CONFIGURATION))
