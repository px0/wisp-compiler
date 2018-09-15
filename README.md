#Wisp Compiler

This lets you compile [wisp](https://gozala.github.io/wisp/) forms (or strings) into Javascript

## Simple Usage
```clojure
  (wisp-compile (foo :bar the-qux))
  ;=> "foo('bar', theQux);"
```

## Why?
This allows you to embed Clojure-flavoured JavaScript into your EDN pages, e.g.:

```clojure
(hiccup/html
  [:html
    [:button {:on-click (wisp-compile (alert (str "Hello " :World )))}
      "Hello!"]
    [:script (wisp-compile (console.log (+ 1 1)))]])
```

# Advanced usage
To use the more advanced wisp features, you'll need to include the runtim, the sequence and string libraries into the page. This should be possible by including the `resources/wisp/{runtime,sequence,string}.js` files into the page (in this order).


Copyright © 2018 Maximilian Gerlach

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
