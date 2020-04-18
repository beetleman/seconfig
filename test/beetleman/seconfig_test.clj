(ns beetleman.seconfig-test
  (:require [beetleman.seconfig :as sut]
            [clojure.test :as t]))


(t/deftest bytes->hex->bytes>hex-test
  (let [{:keys [secret]} (sut/keypair!)
        hex              (sut/bytes->hex secret)]
    (t/is (= hex
             (-> hex
                 sut/hex->bytes
                 sut/bytes->hex)))))


(defn assert-encrypt-decrypt
  [config]
  (let [ci           (sut/keypair!)
        user         (sut/keypair!)
        cipherconfig (sut/encrypt (:secret user)
                                  (:public ci)
                                  config)]
    (t/is (= config
             (sut/decrypt (:public user)
                          (:secret ci)
                          cipherconfig)))))


(t/deftest encrypt-decrypt-test
  (t/testing "flat config with string only values"
             (let [config {"key" "sdsdsdssdsd"
                           :foo  "bar"}]
               (assert-encrypt-decrypt config))))
