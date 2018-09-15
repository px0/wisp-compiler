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

(defmacro wisp-compile
  "Given forms that are correct wisp expressions, returns the compiled JS output:

  (wisp-compile
    (def the-qux 23)
    (foo :bar the-qux))

  ;=> \"var theQux = 23;
  foo('bar', theQux);\""
  [& forms]
  `(wisp-compile-str ~(apply str forms)))
