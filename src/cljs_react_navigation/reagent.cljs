(ns cljs-react-navigation.reagent
  (:require [cljs-react-navigation.base :as base]
            [reagent.core :as r]
            [reagent.impl.component :as ric]
            [cljs.spec.alpha :as s :include-macros true]))


(defn react-component?
  "Same as base, but now accepts a reagent component fn"
  [c]
  (cond
    (ric/react-class? c) c
    (fn? c) (r/reactify-component
              (fn [props & children]
                [c (js->clj props :keywordize-keys true) children]))
    :else :cljs.spec.alpha/invalid))

(defn react-element?
  "Same as base, but now accepts a reagent component fn"
  [e]
  (cond
    (base/isValidElement e) e
    (ric/react-class? e) (r/create-element e)
    (fn? e) (r/create-element
              (r/reactify-component
                (fn [props & children]
                  [e (js->clj props :keywordize-keys true) children])))
    :else :cljs.spec.alpha/invalid))

(defn fn-or-react-component?
  "Same as base, but now *expects* a reagent component if a fn is supplied"
  [fn-or-c]
  (cond
    (ric/react-class? fn-or-c) fn-or-c
    (fn? fn-or-c) (fn [props & children]
                    (let [clj-props (js->clj props :keywordize-keys true)]
                      (r/reactify-component (fn-or-c clj-props children))))
    :else :cljs.spec.alpha/invalid))

(defn fn-or-react-element?
  "Same as base, but now *expects* a reagent component if a fn is supplied"
  [fn-or-e]
  (cond
    (base/isValidElement fn-or-e) fn-or-e
    (ric/react-class? fn-or-e) (r/create-element fn-or-e)
    (fn? fn-or-e) (fn [props & children]
                    (let [clj-props (js->clj props :keywordize-keys true)]
                      (r/as-element [fn-or-e clj-props children])))
    :else :cljs.spec.alpha/invalid))

(defn string-or-react-element? [s-or-e]
  (cond
    (base/isValidElement s-or-e) s-or-e
    (ric/react-class? s-or-e) (r/create-element s-or-e)
    (fn? s-or-e) (r/as-element [(fn [props & children]
                                  (let [clj-props (js->clj props :keywordize-keys true)]
                                    [s-or-e clj-props children]))])
    (string? s-or-e) s-or-e
    :else :cljs.spec.alpha/invalid))

;; Spec overrides for Reagent Components
(s/def :react/component (s/conformer react-component?))
(s/def :react/element (s/conformer react-element?))
(s/def :react-navigation.navigationOptions/headerTitle (s/conformer string-or-react-element?))
(s/def :react-navigation.navigationOptions/headerLeft (s/conformer string-or-react-element?))
(s/def :react-navigation.navigationOptions/headerRight (s/conformer string-or-react-element?))
(s/def :react-navigation.navigationOptions/tabBarIcon (s/conformer fn-or-react-element?))
(s/def :react-navigation.RouteConfigs.route/screen (s/conformer fn-or-react-component?))

;; API
(def NavigationActionsMap base/NavigationActionsMap)
(def stack-screen base/stack-screen)
(def tab-screen base/tab-screen)
(def drawer-component base/drawer-component)
(def stack-navigator base/stack-navigator)
#_(def tab-navigator base/tab-navigator)
(def botton-tab-navigator base/botton-tab-navigator)

(def drawer-navigator base/drawer-navigator)
(def switch-navigator base/switch-navigator)
