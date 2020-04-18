(ns beetleman.seconfig
  (:require [caesium.randombytes :as r]
            [caesium.crypto.box :as cb]
            [caesium.crypto.secretbox :as sb]
            [clojure.walk :as walk])
  (:import (org.apache.commons.codec.binary Hex)))


(defn keypair!
  []
  (cb/keypair! (r/randombytes cb/seedbytes)))


(defn hex->bytes
  [s]
  (.decode (Hex.) (.getBytes s)))

(defn bytes->hex
  [b]
  (Hex/encodeHexString b))



(defn- -encrypt-value
  [user-secret-key ci-public-key value]
  (bytes->hex (cb/encrypt ci-public-key
                          user-secret-key
                          (sb/int->nonce 0)
                          (.getBytes value))))

(defn- -decrypt-value
  [user-public-key ci-secret-key value]
  (cb/decrypt user-public-key
              ci-secret-key
              (sb/int->nonce 0)
              (hex->bytes value)))


(defn encrypt
  [user-secret-key ci-public-key config]
  (walk/postwalk
   (fn [x]
     (if (map? x)
       (into {}
             (map
              (fn [[k v]]
                [k (-encrypt-value user-secret-key ci-public-key v)]))
             x)
       x))
   config))


(defn decrypt
  [user-public-key ci-secret-key cipherconfig]
  (walk/postwalk
   (fn [x]
     (if (map? x)
       (into {}
             (map
              (fn [[k v]]
                [k (String. (-decrypt-value user-public-key ci-secret-key v))]))
             x)
       x))
   cipherconfig))
