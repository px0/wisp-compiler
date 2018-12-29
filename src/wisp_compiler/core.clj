(ns wisp-compiler.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk])
  (:refer-clojure :exclude [compile])
  (:import [javax.script ScriptEngineManager Invocable]))

(def wisp-compiler-file (slurp (io/resource "wisp/dist/wispcompiler.min.js")))

(def wisp-engine
  (let [nashorn (.. (ScriptEngineManager.) (getEngineByName "nashorn"))]
    (.eval nashorn "var window = this;")
    (.eval nashorn "var global = this;")
    (.eval nashorn wisp-compiler-file)
    nashorn))

(defn- strip-exports
  "wisp by default exports things to the 'exports' object - this causes trouble in the browser. Because wisp is written in wisp, there is no easy way to remove this from the compiler directly. It's a dirty hack, but ¯\\_(ツ)_/¯"
  [string]
  (str/replace string #"= exports\..+? =" "="))

(defn evaluate-str
  "Compile a wisp expression string into Javascript. The source-mapping? argument defines whether or not the JS will include mapping data to the wisp expression."
  ([expr source-mapping?]
   (-> (.invokeMethod ^Invocable wisp-engine
                      (.eval wisp-engine "global")
                      "wisp_compile_source_string"
                      (object-array [expr source-mapping?]))
       (strip-exports)))
  ([expr]
   (evaluate-str expr false)))

(defmacro evaluate-forms
  "Given forms that are correct wisp expressions, returns the compiled JS output:

  (evaluate-forms
    (def the-qux 23)
    (foo :bar the-qux))

  ;=> \"var theQux = 23;
  foo('bar', theQux);\"

  Note that this does not evaluate any forms inside the expressions. If you need this, use `compile`
  "
  [& forms]
  `(evaluate-str ~(apply str forms)))

(defmacro compile
  "This partially evaluates expressions in the given wisp forms. You will want
  this if you want to pass arguments to the generated javascript. For example:

  (compile [comment-id \"comment-123\"]
    (let [add-class (fn [el class-name]
                      (.add (.-classList el) class-name))]
      (add-class (document.getElementById comment-id) \"hidden\")))

  will result in this Javascript:

  (function () {
    var addClassø1 = function (el, className) {
        return el.classList.add(className);
    };
    return addClassø1(document.getElementById('comment-123'), 'hidden');
  }.call(this));

  Note that the symbol resolution is symbol-based and dumb, so if you're
  shadowing a variable in a nested scope or something, things will likely break."

  [bindings & forms]
  (let [bindings-map   (->> (partition 2 bindings)
                            (mapv (fn [[k v]]
                                    [(list 'quote k) v]))
                            (into {}))
        bound-forms    `(walk/postwalk-replace ~bindings-map ~(list 'quote forms))
        wisp-forms-str `(apply str ~bound-forms)]
    (list `evaluate-str wisp-forms-str)))

(defn wisp-runtime [] (-> (slurp (io/resource "wisp/runtime.js")) (strip-exports)))
(defn wisp-sequence [] (-> (slurp (io/resource "wisp/sequence.js")) (strip-exports)))
(defn wisp-string [] (-> (slurp (io/resource "wisp/string.js")) (strip-exports)))

(defn wisp-includes []
  (str (wisp-runtime) (wisp-sequence) (wisp-string)))


(defmacro ^:deprecated wisp-compile [& args]
  "Please see the docstring for `evaluate`"
  `(evaluate-forms ~@args))

(defmacro ^:deprecated wisp-compile-str [& args]
  "Please see the dring for `evaluate-str`"
  `(evaluate-str ~@args))
