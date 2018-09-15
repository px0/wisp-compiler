# Wisp Compiler

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
  [:div
    [:button "Hello!"]
    [:script (wisp-compile
      (defn hello-world []
        (alert (str :Hello " World!"))))]])
```

which will compile into
```html
<div>
    <button onclick="helloWorld()">Hello!</button>
    <script>
        var helloWorld = exports.helloWorld = function helloWorld() {
            return alert('' + 'Hello' + ' World!');
        };
        //# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImFub255bW91cy53aXNwIl0sIm5hbWVzIjpbImhlbGxvV29ybGQiLCJleHBvcnRzIiwiYWxlcnQiXSwibWFwcGluZ3MiOiJBQUFBLElBQU1BLFVBQUEsR0FBQUMsT0FBQSxDQUFBRCxVQUFBLEdBQU4sU0FBTUEsVUFBTixHQUFxQjtBQUFBLGVBQUNFLEtBQUQsQyxZQUFPLEdBQVksU0FBbkI7QUFBQSxLQUFyQiIsInNvdXJjZXNDb250ZW50IjpbIihkZWZuIGhlbGxvLXdvcmxkIFtdIChhbGVydCAoc3RyIDpIZWxsbyBcIiBXb3JsZCFcIikpKSJdfQ==
    </script>
</div>
```

# Advanced usage
To use the more advanced wisp features, you'll need to include the runtim, the sequence and string libraries into the page. This should be possible by including the `resources/wisp/{runtime,sequence,string}.js` files into the page (in this order).


Copyright © 2018 Maximilian Gerlach

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
