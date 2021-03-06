# Wisp Compiler

This lets you compile [wisp](https://gozala.github.io/wisp/) forms (or strings) into Javascript

[![Clojars Project](https://img.shields.io/clojars/v/px0/wisp-compiler.svg)](https://clojars.org/px0/wisp-compiler)

## Why?
This allows you to embed Clojure-flavoured JavaScript into your [hiccup](http://weavejester.github.io/hiccup/), pages, e.g.:

```clojure
(require '[wisp-compiler.core :as wisp])

(hiccup/html
  [:div
    [:button {:onclick (wisp/compile [] (hello-world))} "Hello!"]
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

## Okay, but show me something useful
Compiling sexps into JavaScript is neat by itself, but sometimes you want to generate JavaScript dynamically on the server (e.g. for [Server-generated JavaScript Responses](https://signalvnoise.com/posts/3697-server-generated-javascript-responses)). In this case you may have held your nose and just concatenated a bunch of strings. Here is a snippet from one of my projects (don't judge):

```clj
(defn upvote-comment [commentid]
  (javascript-response (str "var newEl = document.createElement('span');"
                            "newEl.innerText = 'Upvoted!';"
                            "var el = document.getElementById(\"" commentid "\").querySelector(\"a.upvote\");"
                            "if(el) el.replaceWith(newEl);"))
```

With wisp, this can (and should!) be rewritten like this:
```clj
(defn upvote-comment [cid]
  (javascript-reponse
    (wisp/compile [commentid cid]
        (let [new-el (document.createElement "span")
              el     (document.getElementById commentid)
              upvote (.querySelector el "a.upvote")]
          (if el
            (el.replaceWith new-el))))))
```

Which will result in JavaScript like this:

```js
// (upvote-comment "my-comment-id")

(function () {
    var newElø1 = document.createElement('span');
    var elø1 = document.getElementById('my-comment-id');
    var upvoteø1 = elø1.querySelector('a.upvote');
    return elø1 ? elø1.replaceWith(newElø1) : void 0;
}.call(this));
```

As you can see, the symbols referenced in the initial binding vector get automatically evaluated and inserted into the expression. This way you can not only write your JavaScript with sweet, sweet parentheses, you will also get the sexps syntax-highlighted, and you don't have to mess around with string manipulation!


# Advanced usage
To use the more advanced Wisp features, you'll need to include the runtime, the sequence and string libraries into the page. This should be possible by including the `resources/wisp/{runtime,sequence,string}.js` files into the page (in this order).

For caching reasons, you should probably just copy them into your `public` folder, but for convenience I am also exposing them as `wisp-runtime`, `wisp-sequence`, `wisp-string`, and altogether as `wisp-includes` functions that return these files as strings

# Gotchas
The symbol of the resolution of the `compile` macro is dumb and will replace the given symbol with its value no matter where in the source code it is. So if you expect to be able to shadow bindings in a nested scope somewhere, it will most likely break. This is for short snippets, and you are very much responsible for reading them after compilation and making sure they work. Don't blame me if your code reaks!

# Compiling wisp
In the `resources/wisp` directory, run `make compiler` to re-compile the wisp compiler


Copyright © 2018 Maximilian Gerlach

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
