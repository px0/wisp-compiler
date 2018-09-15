(ns wisp-compiler.core-test
  (:require [clojure.test :refer :all]
            [wisp-compiler.core :refer :all]
            [clojure.string :as str]))

(deftest str-test
  (let [js (wisp-compile "(+ 1 1)")]
    (is (str/starts-with? js "1 + 1;"))))

(deftest form-test
  (let [js (wisp-compile (+ 1 1))]
    (is (str/starts-with? js "1 + 1;"))))
