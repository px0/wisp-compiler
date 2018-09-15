# Wisp Compiler

This lets you compile [wisp](https://gozala.github.io/wisp/) forms (or strings) into Javascript

## Simple Usage
```clojure
  (wisp-compile (foo :bar the-qux))
  ;=> "foo('bar', theQux);"
```

## Why?
This allows you to embed Clojure-flavoured JavaScript into your [hiccup](http://weavejester.github.io/hiccup/), pages, e.g.:

```clojure
(hiccup/html
  [:div
    [:button {:onclick (wisp-compile (hello-world))} "Hello!"]
    [:script (wisp-compile
      (defn hello-world []
        (alert (str :Hello " World!"))))]])
```

which will compile into
```html
<div>
    <button onclick="helloWorld();">Hello!</button>
    <script>
        var helloWorld = function helloWorld() {
            return alert('' + 'Hello' + ' World!');
        };
    </script>
</div>
```

# Advanced usage
To use the more advanced wisp features, you'll need to include the runtime, the sequence and string libraries into the page. This should be possible by including the `resources/wisp/{runtime,sequence,string}.js` files into the page (in this order).

For caching reasons, you should probably just copy them into your `public` folder, but for convenience I am also exposing them as `wisp-runtime`, `wisp-sequence`, `wisp-string`, and altogether as `wisp-includes` functions that return these files as strings


# Compiling wisp
In the `resources/wisp` directory, run `make compiler` to re-compile the wisp compiler


Copyright © 2018 Maximilian Gerlach

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
