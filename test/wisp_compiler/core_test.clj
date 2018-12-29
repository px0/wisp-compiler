(ns wisp-compiler.core-test
  (:require [clojure.test :refer :all]
            [wisp-compiler.core :as wisp]
            [clojure.string :as str]))

(deftest deprected-str-test
  (let [js (wisp/wisp-compile "(+ 1 1)")]
    (is (str/starts-with? js "1 + 1;"))))

(deftest deprecated-form-test
  (let [js (wisp/wisp-compile (+ 1 1))]
    (is (str/starts-with? js "1 + 1;"))))

(deftest str-test
  (let [js (wisp/evaluate-str "(+ 1 1)")]
    (is (str/starts-with? js "1 + 1;"))))

(deftest form-test
  (let [js (wisp/evaluate-forms (+ 1 1))]
    (is (str/starts-with? js "1 + 1;"))))

(deftest no-binding-test
  (let [js (wisp/compile [] (+ x 5 z))]
    (is (str/starts-with? js "x + 5 + z;"))))

(deftest simple-binding-test
  (let [js (wisp/compile [x 23]
                        (+ x 5 z))]
    (is (str/starts-with? js "23 + 5 + z;"))))

(deftest complex-binding-test
  (let [js (wisp/compile [x (* 2 5)] (+ x 5 z))]
    (is (str/starts-with? js "10 + 5 + z;"))))

(deftest real-life-binding-test
  (let [comment-id-from-elsewhere "comment-123"]
    (let [js (wisp/compile [comment-id comment-id-from-elsewhere]
                           (let [add-class (fn [el class-name]
                                             (.add (.-classList el) class-name))])
                           (add-class (document.getElementById comment-id) "hidden"))]

      (is (= js "(function () {
    var addClassø1 = function (el, className) {
        return el.classList.add(className);
    };
    return void 0;
}.call(this));
addClass(document.getElementById('comment-123'), 'hidden');")))))

(deftest real-life-complex-binding-test
  (let [comment-id-from-elsewhere "comment-123"
        some-class "hidden"]
    (let [js (wisp/compile [comment-id comment-id-from-elsewhere
                            the-class some-class]
                           (let [add-class (fn [el class-name]
                                             (.add (.-classList el) class-name))])
                           (add-class (document.getElementById comment-id) the-class))]
      (is (= js "(function () {\n    var addClassø1 = function (el, className) {\n        return el.classList.add(className);\n    };\n    return void 0;\n}.call(this));\naddClass(document.getElementById('comment-123'), 'hidden');")))))
