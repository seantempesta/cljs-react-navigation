(defproject cljs-react-navigation "0.1.5-SNAPSHOT"
  :description "CLJS Wrappers for react-navigation"
  :url "https://github.com/seantempesta/cljs-react-navigation"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [reagent "0.7.0" :exclusions [cljsjs/react
                                               cljsjs/react-dom
                                               cljsjs/react-dom-server
                                               cljsjs/create-react-class]]
                 [re-frame "0.10.4"]]
  :plugins [[lein-codox "0.10.3"]]
  :codox {:language :clojurescript}
  :source-paths ["src"])
