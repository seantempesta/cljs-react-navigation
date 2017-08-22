(defproject cljs-react-navigation "0.1.1"
  :description "CLJS Wrappers for react-navigation"
  :url "https://github.com/seantempesta/cljs-react-navigation"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.542"]
                 [reagent "0.6.1" :exclusions [cljsjs/react
                                               cljsjs/react-dom
                                               cljsjs/react-dom-server]]
                 [re-frame "0.9.2"]]
  :plugins [[lein-codox "0.10.3"]]
  :codox {:language :clojurescript}
  :source-paths ["src"])
