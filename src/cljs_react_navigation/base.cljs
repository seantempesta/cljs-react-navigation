(ns cljs-react-navigation.base
  (:require [cljs.spec.alpha :as s :include-macros true]
            [goog.object :as gobj]
            [reagent.core :as r]
            [reagent.impl.component :as ric]))

(defonce React (js/require "react"))
(defonce ReactNavigation (js/require "react-navigation"))
(defonce isValidElement (gobj/get React #js ["isValidElement"]))

;; Core
(defonce createNavigationContainer (gobj/get ReactNavigation #js ["createNavigationContainer"]))
(defonce StateUtils (gobj/get ReactNavigation #js ["StateUtils"]))
(defonce addNavigationHelpers (gobj/get ReactNavigation #js ["addNavigationHelpers"]))
(defonce NavigationActions (gobj/get ReactNavigation #js ["NavigationActions"]))
(defonce NavigationActionsMap {"Navigation/INIT"       (gobj/get NavigationActions #js ["init"])
                               "Navigation/URI"        (gobj/get NavigationActions #js ["uri"])
                               "Navigation/NAVIGATE"   (gobj/get NavigationActions #js ["navigate"])
                               "Navigation/BACK"       (gobj/get NavigationActions #js ["back"])
                               "Navigation/RESET"      (gobj/get NavigationActions #js ["reset"])
                               "Navigation/SET_PARAMS" (gobj/get NavigationActions #js ["setParams"])})

;; Navigators
(defonce createNavigator (gobj/get ReactNavigation #js ["createNavigator"]))
(defonce StackNavigator (gobj/get ReactNavigation #js ["StackNavigator"]))
(defonce TabNavigator (gobj/get ReactNavigation #js ["TabNavigator"]))
(defonce DrawerNavigator (gobj/get ReactNavigation #js ["DrawerNavigator"]))
(defonce SwitchNavigator (gobj/get ReactNavigation #js ["SwitchNavigator"]))

;; Routers
(defonce StackRouter (gobj/get ReactNavigation #js ["StackRouter"]))
(defonce TabRouter (gobj/get ReactNavigation #js ["TabRouter"]))

;; Views
(defonce Transitioner (gobj/get ReactNavigation #js ["Transitioner"]))
(defonce CardStack (gobj/get ReactNavigation #js ["CardStack"]))
(defonce DrawerView (gobj/get ReactNavigation #js ["DrawerView"]))
(defonce TabView (gobj/get ReactNavigation #js ["TabView"]))

(assert (and React ReactNavigation) "React and React Navigation must be installed.  Maybe NPM install them and restart the packager?")

;; Spec conforming functions
;;

(defn react-component?
  "Spec conforming function.  Accepts a react class or returns :cljs.spec.alpha/invalid."
  [c]
  (cond
    (ric/react-class? c) c
    :else :cljs.spec.alpha/invalid))

(defn react-element?
  "Spec conforming function.  Accepts either a react element, conforms a react class to an element, or returns :cljs.spec.alpha/invalid."
  [e]
  (cond
    (isValidElement e) e
    (ric/react-class? e) (r/create-element e)
    :else :cljs.spec.alpha/invalid))

(defn string-or-react-component?
  "Accepts either a string, react component, or a fn that returns a react component.  If it's a fn, props will automatically
  convert (js->clj) when the fn is called."
  [s-or-c]
  (cond
    (ric/react-class? s-or-c) s-or-c
    (string? s-or-c) s-or-c
    (fn? s-or-c) (fn [props & children]
                   (let [clj-props (js->clj props :keywordize-keys true)
                         react-c (s-or-c clj-props children)]
                     react-c))
    :else :cljs.spec.alpha/invalid))

(defn string-or-react-element? [s-or-e]
  (cond
    (isValidElement s-or-e) s-or-e
    (ric/react-class? s-or-e) (r/create-element s-or-e)
    (fn? s-or-e) (fn [props & children]
                   (let [clj-props (js->clj props :keywordize-keys true)
                         react-c (s-or-e clj-props children)
                         react-e (r/create-element react-c)]
                     react-e))
    (string? s-or-e) s-or-e
    :else :cljs.spec.alpha/invalid))

(defn fn-or-react-component?
  "Confirms either a valid react component was passed in or a function that returns a react component.
  If it's a function, props will be converted to clojure structures with keywords. Expects the fn to return a valid component"
  [fn-or-c]
  (cond
    (ric/react-class? fn-or-c) fn-or-c
    (fn? fn-or-c) (fn [props & children]
                    (let [clj-props (js->clj props :keywordize-keys true)
                          react-c (fn-or-c clj-props children)]
                      react-c))
    :else :cljs.spec.alpha/invalid))

(defn fn-or-react-element?
  "Confirms either a valid react element was passed in or a function that returns a react element.
  If it's a function, props will be converted to clojure structures with keywords. Expects the fn to return a valid component"
  [fn-or-e]
  (cond
    (isValidElement fn-or-e) fn-or-e
    (ric/react-class? fn-or-e) (r/create-element fn-or-e)
    (fn? fn-or-e) (fn [props & children]
                    (let [clj-props (js->clj props :keywordize-keys true)
                          react-c (fn-or-e clj-props children)
                          react-e (r/create-element react-c)]
                      react-e))
    :else :cljs.spec.alpha/invalid))

(defn navigation-options?
  "Conforms a clj map (or a function that returns a clj map) to a js object"
  [map-or-fn]
  (cond
    (map? map-or-fn) (s/conform :react-navigation.navigationOptions/all map-or-fn)
    (fn? map-or-fn) (fn [props]
                      (let [clj-props (js->clj props :keywordize-keys true)
                            fn-output (map-or-fn clj-props)
                            conformed (s/conform :react-navigation.navigationOptions/all fn-output)
                            conformed-js (clj->js conformed)]
                        conformed-js))
    :else :cljs.spec.alpha/invalid))


;; React
(s/def :react/component (s/conformer react-component?))
(s/def :react/element (s/conformer react-element?))

;; navigationOptions (merged both Screen and Tabs for my sanity)
(s/def :react-navigation.navigationOptions/title string?)
(s/def :react-navigation.navigationOptions/headerVisible boolean?)
(s/def :react-navigation.navigationOptions/headerTitle (s/conformer string-or-react-element?))
(s/def :react-navigation.navigationOptions/headerBackTitle string?)
(s/def :react-navigation.navigationOptions/headerRight (s/conformer string-or-react-element?))
(s/def :react-navigation.navigationOptions/headerLeft (s/conformer string-or-react-element?))
(s/def :react-navigation.navigationOptions/headerStyle map?)
(s/def :react-navigation.navigationOptions/headerTitleStyle map?)
(s/def :react-navigation.navigationOptions/headerTintColor string?)
(s/def :react-navigation.navigationOptions/headerPressColorAndroid string?)
(s/def :react-navigation.navigationOptions/gesturesEnabled boolean?)
(s/def :react-navigation.navigationOptions/tabBarVisible boolean?)
(s/def :react-navigation.navigationOptions/tabBarIcon (s/conformer fn-or-react-element?))
(s/def :react-navigation.navigationOptions/tabBarLabel string?)
(s/def :react-navigation.navigationOptions/all (s/keys :opt-un [:react-navigation.navigationOptions/title
                                                                :react-navigation.navigationOptions/headerVisible
                                                                :react-navigation.navigationOptions/headerTitle
                                                                :react-navigation.navigationOptions/headerBackTitle
                                                                :react-navigation.navigationOptions/headerRight
                                                                :react-navigation.navigationOptions/headerLeft
                                                                :react-navigation.navigationOptions/headerStyle
                                                                :react-navigation.navigationOptions/headerTitleStyle
                                                                :react-navigation.navigationOptions/headerTintColor
                                                                :react-navigation.navigationOptions/headerPressColorAndroid
                                                                :react-navigation.navigationOptions/gesturesEnabled
                                                                :react-navigation.navigationOptions/tabBarVisible
                                                                :react-navigation.navigationOptions/tabBarIcon
                                                                :react-navigation.navigationOptions/tabBarLabel]))
(s/def :react-navigation/navigationOptions (s/nilable (s/conformer navigation-options?)))

;; RouteConfigs
(s/def :react-navigation.RouteConfigs.route/screen (s/conformer fn-or-react-component?))
(s/def :react-navigation.RouteConfigs.route/path string?)
(s/def :react-navigation.RouteConfigs/route (s/keys :req-un [:react-navigation.RouteConfigs.route/screen]
                                                    :opt-un [:react-navigation.RouteConfigs.route/path
                                                             :react-navigation/navigationOptions]))
(s/def :react-navigation/RouteConfigs (s/map-of keyword? :react-navigation.RouteConfigs/route))

;; StackNavigator StackNavigatorConfig
(s/def :react-navigation.StackNavigator.StackNavigatorConfig/initialRouteName string?)
(s/def :react-navigation.StackNavigator.StackNavigatorConfig/initialRouteParams map?)
(s/def :react-navigation.StackNavigator.StackNavigatorConfig/paths map?)
(s/def :react-navigation.StackNavigator.StackNavigatorConfig/mode #{"modal" "card"})
(s/def :react-navigation.StackNavigator.StackNavigatorConfig/headerMode #{"float" "screen" "none"})
(s/def :react-navigation.StackNavigator.StackNavigatorConfig/cardStyle map?)
(s/def :react-navigation.StackNavigator.StackNavigatorConfig/onTransitionStart fn?)
(s/def :react-navigation.StackNavigator.StackNavigatorConfig/onTransitionEnd fn?)
(s/def :react-navigation.StackNavigator/StackNavigatorConfig (s/nilable (s/keys :opt-un [:react-navigation.StackNavigator.StackNavigatorConfig/initialRouteName
                                                                                         :react-navigation.StackNavigator.StackNavigatorConfig/initialRouteParams
                                                                                         :react-navigation/navigationOptions
                                                                                         :react-navigation.StackNavigator.StackNavigatorConfig/paths
                                                                                         :react-navigation.StackNavigator.StackNavigatorConfig/mode
                                                                                         :react-navigation.StackNavigator.StackNavigatorConfig/headerMode
                                                                                         :react-navigation.StackNavigator.StackNavigatorConfig/cardStyle
                                                                                         :react-navigation.StackNavigator.StackNavigatorConfig/onTransitionStart
                                                                                         :react-navigation.StackNavigator.StackNavigatorConfig/onTransitionEnd])))

;; TabNavigator TabNavigatorConfig
(s/def :react-navigation.TabNavigator.TabNavigatorConfig/tabBarComponent (s/conformer fn-or-react-component?))
(s/def :react-navigation.TabNavigator.TabNavigatorConfig/tabBarPosition #{"top" "bottom"})
(s/def :react-navigation.TabNavigator.TabNavigatorConfig/swipeEnabled boolean?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig/animationEnabled boolean?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig/lazyLoad boolean?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig/initialRouteName string?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig/order (s/coll-of string?))
(s/def :react-navigation.TabNavigator.TabNavigatorConfig/paths map?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig/backBehavior #{"initialroute" "none"})
(s/def :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/activeTintColor string?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/activeBackgroundColor string?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/inactiveTintColor string?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/inactiveBackgroundColor string?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/showLabel boolean?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/style map?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/labelStyle map?)
(s/def :react-navigation.TabNavigator.TabNavigatorConfig/tabBarOptions (s/nilable (s/keys :opt-un [:react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/activeTintColor
                                                                                                   :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/activeBackgroundColor
                                                                                                   :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/inactiveTintColor
                                                                                                   :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/inactiveBackgroundColor
                                                                                                   :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/showLabel
                                                                                                   :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/style
                                                                                                   :react-navigation.TabNavigator.TabNavigatorConfig.tabBarOptions/labelStyle])))

(s/def :react-navigation.TabNavigator/TabNavigatorConfig (s/nilable (s/keys :opt-un [:react-navigation.TabNavigator.TabNavigatorConfig/tabBarComponent
                                                                                     :react-navigation.TabNavigator.TabNavigatorConfig/tabBarPosition
                                                                                     :react-navigation.TabNavigator.TabNavigatorConfig/swipeEnabled
                                                                                     :react-navigation.TabNavigator.TabNavigatorConfig/animationEnabled
                                                                                     :react-navigation.TabNavigator.TabNavigatorConfig/lazyLoad
                                                                                     :react-navigation.TabNavigator.TabNavigatorConfig/initialRouteName
                                                                                     :react-navigation.TabNavigator.TabNavigatorConfig/order
                                                                                     :react-navigation.TabNavigator.TabNavigatorConfig/paths
                                                                                     :react-navigation.TabNavigator.TabNavigatorConfig/backBehavior
                                                                                     :react-navigation.TabNavigator.TabNavigatorConfig/tabBarOptions])))

;; DrawerNavigator DrawerNavigatorConfig
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig/drawerWidth number?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig/drawerPosition #{"left" "right"})
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig/contentComponent (s/conformer fn-or-react-component?))
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig/useNativeAnimations boolean?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig/drawerBackgroundColor string?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig/initialRouteName string?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig/order (s/coll-of string?))
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig/paths map?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig/backBehavior #{"initialroute" "none"})
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/activeTintColor string?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/activeBackgroundColor string?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/inactiveTintColor string?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/inactiveBackgroundColor string?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/onItemPress (s/conformer fn-or-react-component?))
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/itemsContainerStyle map?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/iconContainerStyle map?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/labelStyle map?)
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/itemStyle (s/conformer string-or-react-element?))
(s/def :react-navigation.DrawerNavigator.DrawerNavigatorConfig/contentOptions (s/nilable (s/keys :opt-un [:react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/activeTintColor
                                                                                                          :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/activeBackgroundColor
                                                                                                          :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/inactiveTintColor
                                                                                                          :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/inactiveBackgroundColor
                                                                                                          :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/onItemPress
                                                                                                          :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/itemsContainerStyle
                                                                                                          :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/iconContainerStyle
                                                                                                          :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/labelStyle
                                                                                                          :react-navigation.DrawerNavigator.DrawerNavigatorConfig.contentOptions/itemStyle])))
(s/def :react-navigation.DrawerNavigator/DrawerNavigatorConfig (s/nilable (s/keys :opt-un [:react-navigation.DrawerNavigator.DrawerNavigatorConfig/drawerWidth
                                                                                           :react-navigation.DrawerNavigator.DrawerNavigatorConfig/drawerPosition
                                                                                           :react-navigation.DrawerNavigator.DrawerNavigatorConfig/contentComponent
                                                                                           :react-navigation.DrawerNavigator.DrawerNavigatorConfig/contentOptions
                                                                                           :react-navigation.DrawerNavigator.DrawerNavigatorConfig/useNativeAnimations
                                                                                           :react-navigation.DrawerNavigator.DrawerNavigatorConfig/drawerBackgroundColor
                                                                                           :react-navigation.DrawerNavigator.DrawerNavigatorConfig/initialRouteName
                                                                                           :react-navigation.DrawerNavigator.DrawerNavigatorConfig/order
                                                                                           :react-navigation.DrawerNavigator.DrawerNavigatorConfig/paths
                                                                                           :react-navigation.DrawerNavigator.DrawerNavigatorConfig/backBehavior])))

(s/def :react-navigation.SwitchNavigator.SwitchNavigatorConfig/initialRouteName string?)
(s/def :react-navigation.SwitchNavigator.SwitchNavigatorConfig/resetOnBlur boolean?)
(s/def :react-navigation.SwitchNavigator.SwitchNavigatorConfig/paths map?)
(s/def :react-navigation.SwitchNavigator.SwitchNavigatorConfig/backBehavior #{"initialroute" "none"})
(s/def :react-navigation.SwitchNavigator/SwitchNavigatorConfig (s/nilable (s/keys :opt-un [:react-navigation.SwitchNavigator.SwitchNavigatorConfig/initialRouteName
                                                                                           :react-navigation.SwitchNavigator.SwitchNavigatorConfig/resetOnBlur
                                                                                           :react-navigation.SwitchNavigator.SwitchNavigatorConfig/paths
                                                                                           :react-navigation.SwitchNavigator.SwitchNavigatorConfig/backBehavior])))

(defn append-navigationOptions
  "If navigationOptions are specified append to the react-component"
  [react-component navigationOptions]

  (when (and navigationOptions (not= navigationOptions :cljs.spec.alpha/invalid))
    (aset react-component "navigationOptions" (clj->js navigationOptions)))

  react-component)

(s/fdef append-navigationOptions
        :args (s/tuple :react/component :react-navigation/navigationOptions)
        :ret #(ric/react-class? %))

(defn stack-screen [react-component navigationOptions]
  (let [react-component-conformed (s/conform :react/component react-component)
        navigationOptions-conformed (s/conform :react-navigation/navigationOptions navigationOptions)]
    (assert (not= react-component-conformed :cljs.spec.alpha/invalid) "Invalid react component.")
    (assert (not= navigationOptions-conformed :cljs.spec.alpha/invalid) "Invalid navigationOptions.")
    (append-navigationOptions react-component-conformed navigationOptions-conformed)))

(defn tab-screen [react-component navigationOptions]
  (let [react-component-conformed (s/conform :react/component react-component)
        navigationOptions-conformed (s/conform :react-navigation/navigationOptions navigationOptions)]
    (assert (not= react-component-conformed :cljs.spec.alpha/invalid) "Invalid react component.")
    (assert (not= navigationOptions-conformed :cljs.spec.alpha/invalid) "Invalid navigationOptions.")
    (append-navigationOptions react-component-conformed navigationOptions-conformed)))

(defn drawer-component [react-component navigationOptions]
  (let [react-component-conformed (s/conform :react/component react-component)
        navigationOptions-conformed (s/conform :react-navigation/navigationOptions navigationOptions)]
    (assert (not= react-component-conformed :cljs.spec.alpha/invalid) "Invalid react component.")
    (assert (not= navigationOptions-conformed :cljs.spec.alpha/invalid) "Invalid navigationOptions.")
    (append-navigationOptions react-component-conformed navigationOptions-conformed)))

;; Navigators
;;
(defn stack-navigator [routeConfigs stackNavigatorConfig]
  (let [routeConfigs-conformed (s/conform :react-navigation/RouteConfigs routeConfigs)
        StackNavigatorConfig-conformed (s/conform :react-navigation.StackNavigator/StackNavigatorConfig stackNavigatorConfig)]
    (assert (not= routeConfigs-conformed :cljs.spec.alpha/invalid))
    (assert (not= StackNavigatorConfig-conformed :cljs.spec.alpha/invalid))
    (if StackNavigatorConfig-conformed
      (StackNavigator (clj->js routeConfigs-conformed) (clj->js StackNavigatorConfig-conformed))
      (StackNavigator (clj->js routeConfigs-conformed)))))

(defn tab-navigator [routeConfigs navigationOptions]
  (let [routeConfigs-conformed (s/conform :react-navigation/RouteConfigs routeConfigs)
        navigationOptions-conformed (s/conform :react-navigation/navigationOptions navigationOptions)]
    (assert (not= routeConfigs-conformed :cljs.spec.alpha/invalid))
    (assert (not= navigationOptions-conformed :cljs.spec.alpha/invalid))
    (TabNavigator (clj->js routeConfigs-conformed) (clj->js navigationOptions-conformed))))

(defn drawer-navigator [routeConfigs drawerNavigatorConfig]
  (let [routeConfigs-conformed (s/conform :react-navigation/RouteConfigs routeConfigs)
        drawerNavigatorConfig-conformed (s/conform :react-navigation.DrawerNavigator/DrawerNavigatorConfig drawerNavigatorConfig)]
    (assert (not= routeConfigs-conformed :cljs.spec.alpha/invalid))
    (assert (not= drawerNavigatorConfig-conformed :cljs.spec.alpha/invalid))
    (if drawerNavigatorConfig-conformed
      (DrawerNavigator (clj->js routeConfigs-conformed) (clj->js drawerNavigatorConfig-conformed))
      (DrawerNavigator (clj->js routeConfigs-conformed)))))

(defn switch-navigator [routeConfigs switchNavigatorConfig]
  (let [routeConfigs-conformed (s/conform :react-navigation/RouteConfigs routeConfigs)
        switchNavigatorConfig-conformed (s/conform :react-navigation.SwitchNavigator/SwitchNavigatorConfig switchNavigatorConfig)]
    (assert (not= routeConfigs-conformed :cljs.spec.alpha/invalid))
    (assert (not= switchNavigatorConfig-conformed :cljs.spec.alpha/invalid))
    (if switchNavigatorConfig-conformed
      (SwitchNavigator (clj->js routeConfigs-conformed) (clj->js switchNavigatorConfig-conformed))
      (SwitchNavigator (clj->js routeConfigs-conformed)))))
