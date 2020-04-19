(ns beetleman.seconfig.core-test
  (:require [beetleman.seconfig.core :as sut]
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


(t/deftest -with-prefix-trim-prefix-test
  (let [t        "some random text"
        prefixed (#'sut/-with-prefix t)]
    (t/is (not= t
                prefixed))
    (t/is (= t
             (#'sut/-trim-prefix prefixed)))))


(t/deftest encrypt-decrypt-test
  (t/testing "flat config with string only values"
             (let [config {"key" "sdsdsdssdsd"
                           :foo  "bar"}]
               (assert-encrypt-decrypt config)))

  (t/testing "flat config with string and numbers"
             (let [config {:string          "sdsdsdssdsd"
                           :number          1
                           :negative/number -33
                           :float           0.1212}]
               (assert-encrypt-decrypt config)))

  
  (t/testing "not encrypted values are ignored during decryption"
             (let [config       {:key "value"}
                   ci           (sut/keypair!)
                   user         (sut/keypair!)
                   cipherconfig (sut/encrypt (:secret user)
                                             (:public ci)
                                             config)
                   other-config {:other-key "value"}]
               (t/is (= (merge config
                               other-config)
                        (sut/decrypt (:public user)
                                     (:secret ci)
                                     (merge cipherconfig
                                            other-config)))))))
