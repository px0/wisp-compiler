(defproject px0/wisp-compiler "0.3.0"
  :description "Compile wisp expressions to JavaScript in your Clojure project!"
  :url "https://github.com/px0/wisp-compiler"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories [["clojars"  {:sign-releases false
                                     :url "https://clojars.org/repo"}]]
  :release-tasks [["deploy"]]
  :dependencies [[org.clojure/clojure "1.8.0"]])
