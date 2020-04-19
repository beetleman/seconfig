(ns beetleman.seconfig.core
  (:require [caesium.randombytes :as r]
            [caesium.crypto.box :as cb]
            [caesium.crypto.secretbox :as sb]
            [clojure.edn :as edn]
            [clojure.walk :as walk]
            [clojure.tools.logging :as log]
            [clojure.string :as string])
  (:import (org.apache.commons.codec.binary Hex)))


(defn hex->bytes
  [s]
  (.decode (Hex.) (.getBytes s)))

(defn bytes->hex
  [b]
  (Hex/encodeHexString b))


(def prefix (str ::encrypted ":"))


(defn- -with-prefix
  [value]
  (str prefix value))


(def -trim-prefix-re (re-pattern (str "^" prefix)))

(defn- -trim-prefix
  [value]
  (string/replace-first value -trim-prefix-re ""))


(defn keypair!
  []
  (into {}
        (map (fn [[k v]] [k (bytes->hex v)]))
        (cb/keypair! (r/randombytes cb/seedbytes))))


(defn- -encrypt-it?
  [value]
  (or (number? value)
      (string? value)))


(defn- -decrypt-it?
  [value]
  (and (string? value)
       (string/starts-with? value prefix)))


(defn- -encrypt-value
  [user-secret-key ci-public-key value]
  (let [user-secret-key (hex->bytes user-secret-key)
        ci-public-key   (hex->bytes ci-public-key)]
    (if (-encrypt-it? value)
      (->> value
           pr-str
           (.getBytes)
           (cb/encrypt ci-public-key
                       user-secret-key
                       (sb/int->nonce 0))
           bytes->hex
           -with-prefix)
      (do
        (log/warn "Value ignored" {:value value})
        value))))


(defn- -decrypt-value
  [user-public-key ci-secret-key value]
  (let [user-public-key (hex->bytes user-public-key)
        ci-secret-key   (hex->bytes ci-secret-key)]
    (if (-decrypt-it? value)
      (->> value
           -trim-prefix
           hex->bytes
           (cb/decrypt user-public-key
                       ci-secret-key
                       (sb/int->nonce 0))
           (String.)
           edn/read-string)
      value)))


(defn- -postwalk-values
  [f form]
  (walk/postwalk
   (fn [x]
     (if (map? x)
       (into {}
             (map (fn [[k v]]
                    [k (f v)]))
             x)
       x))
   form))


(defn encrypt
  [user-secret-key ci-public-key config]
  (-postwalk-values
   (fn [v] (-encrypt-value user-secret-key ci-public-key v))
   config))


(defn decrypt
  [user-public-key ci-secret-key cipherconfig]
  (-postwalk-values
   (fn [v] (-decrypt-value user-public-key ci-secret-key v))
   cipherconfig))
