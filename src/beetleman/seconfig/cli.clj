(ns beetleman.seconfig.cli
  (:require [beetleman.seconfig.core :as seconfig]
            [cli-matic.core :refer [run-cmd]]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]))


(defn encrypt
  [{:keys [input user-sk ci-pk]}]
  (pprint/pprint (seconfig/encrypt user-sk ci-pk input)))


(defn decrypt
  [{:keys [input user-pk ci-sk]}]
  (pprint/pprint (seconfig/decrypt user-pk ci-sk input)))


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
     :opts        [{:option  "input"
                    :short   "i"
                    :as      "Input path"
                    :default :present
                    :type    :ednfile}
                   {:option  "user-sk"
                    :short   "u"
                    :env     "USER_SK"
                    :as      "User secret key"
                    :default :present
                    :type    :string}
                   {:option  "ci-pk"
                    :short   "c"
                    :env     "CI_PK"
                    :as      "CI publick key"
                    :default :present
                    :type    :string}]
     :runs        encrypt}
    {:command     "decrypt"
     :description "Decrypt config"
     :opts        [{:option  "input"
                    :short   "i"
                    :as      "Input path"
                    :default :present
                    :type    :ednfile}
                   {:option  "user-pk"
                    :short   "u"
                    :env     "USER_PK"
                    :as      "User public key"
                    :default :present
                    :type    :string}
                   {:option  "ci-sk"
                    :short   "c"
                    :env     "CI_SK"
                    :as      "CI secret key"
                    :default :present
                    :type    :string}]
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
