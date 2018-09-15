(ns wisp-compiler.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [javax.script ScriptEngineManager Invocable]))

(def wisp-compiler-file (slurp (io/resource "wisp/dist/wispcompiler.min.js")))

(def wisp-engine
  (let [nashorn (.. (ScriptEngineManager.) (getEngineByName "nashorn"))]
    (.eval nashorn "var window = this;")
    (.eval nashorn "var global = this;")
    (.eval nashorn wisp-compiler-file)
    nashorn))
;;TODO  investigate {:no-map true}"

(defn strip-exports
  "wisp by default exports things to the 'exports' object - this causes trouble in the browser. Because wisp is written in wisp, there is no easy way to remove this from the compiler directly. It's a dirty hack, but ¯\\_(ツ)_/¯"
  [string]
  (str/replace string #"= exports\..+? =" "="))

(defn wisp-compile-str
  ([expr mapping?]
   (-> (.invokeMethod ^Invocable wisp-engine
                      (.eval wisp-engine "global")
                      "wisp_compile_source_string"
                      (object-array [expr mapping?]))
       (strip-exports)))
  ([expr]
   (wisp-compile-str expr false)))

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
