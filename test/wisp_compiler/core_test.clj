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

(deftest binding-test
  (let [js (wisp-compile-bind [comment-id "comment-123"]
                              (let [add-class (fn [el class-name]
                                                (.add (.-classList el) class-name))])
                              (add-class (document.getElementById comment-id) "hidden"))]

    (is (= js "(function () {\n    var addClassø1 = function (el, className) {\n        return el.classList.add(className);\n    };\n    return void 0;\n}.call(this));\naddClass(document.getElementById('comment-123'), 'hidden');"
          ))))
