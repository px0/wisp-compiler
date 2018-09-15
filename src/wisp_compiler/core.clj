(ns wisp-compiler.core
  (:require [clojure.java.io :as io])
  (:import [javax.script ScriptEngineManager Invocable]))

(def wisp-compiler-file (slurp (io/resource "wisp/dist/wispcompiler.min.js")))

(def wisp-engine
  (let [nashorn (.. (ScriptEngineManager.) (getEngineByName "nashorn"))]
    (.eval nashorn "var window = this;")
    (.eval nashorn "var global = this;")
    (.eval nashorn wisp-compiler-file)
    nashorn))

(defn wisp-compile-str [expr]
  (.invokeMethod ^Invocable wisp-engine
                 (.eval wisp-engine "global")
                 "wisp_compile_source_string"
                 (object-array [expr])))

(defmacro wisp-compile-form
  [form]
  `(wisp-compile-str ~(str form)))

(defmacro wisp-compile
  "Given a string or a form that is a correct wisp expression, returns the compiled JS output:

  (wisp-compile (foo :bar the-qux))
  => \"foo('bar', theQux); \""

  [body]
  (if (string? body)
    (wisp-compile-str body)
    `(wisp-compile-form ~body)))
