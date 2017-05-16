# cljs-react-navigation

This library is my attempt to unify all `cljs` `react-navigation` libraries under a shared code base.  Right now it only includes `Reagent/Re-Frame` libraries, but I'm hoping other people will help me add `om-next` and `rum` libraries.   

![](https://clojars.org/cljs-react-navigation/latest-version.svg)

![](https://media.giphy.com/media/3o7bu14rq4AVqTK1Nu/giphy.gif)

## Dependencies

Clojurescript
```clojure
[org.clojure/clojurescript "1.9.542"] ;; using cljs.spec.alpha namespace
[reagent "0.6.1"]                     ;; Import the cljs-react-navigation.reagent namespace
[re-frame "0.9.2"]                    ;; Import the cljs-react-navigation.re-frame namespace
```

Javascript
```js
"react": "16.0.0-alpha.6",
"react-native": "0.44.0",
"react-navigation": "^1.0.0-beta.9"
```

## Do this first

Add `react-navigation` to your project.
```
npm install react-navigation --save
```

## Documentation

Anything `react-navigation` related is documented on [their site](https://reactnavigation.org/docs/intro/).  The idea for this library isn't to re-invent the wheel, but make it cljs friendly.  

Specifically:
- Conforming whatever cljs wrapper components into the correct react components/elements
- Converting props to cljs data structures to avoid `clj->js` & `js->clj`ing everything.


## Usage for base

Yeah, you really don't use this directly.  Try extending this set of specs and and functions.  See the `reagent` version.

That being said, I'm literally just spec'ing out the javascript API, with some minor tweaks:
- `screen` function isn't overloaded (there's a `stack-screen` and a `tab-screen`)
- `screen` functions accepts the `navigationOptions` as a second param (god knows why they insist on doing ES6 style initializations) 

## Usage for reagent

First import the reagent version of the library library:
```clojure
(ns uiexplorer.routing
  (:require [cljs-react-navigation.reagent :refer [stack-navigator tab-navigator stack-screen tab-screen router]]))
           
```
 [cljs-react-navigation.re-frame :refer [stack-navigator tab-navigator stack-screen tab-screen router]]
           

The basic idea is to create reagent-components that accept props like this:

```clojure
(defn home
  "Each Screen will receive two props:
 - screenProps - Extra props passed down from the router (rarely used)
 - navigation  - The main navigation functions in a map as follows:
   {:state     - routing state for this screen
    :dispatch  - generic dispatch fn
    :goBack    - pop's the current screen off the stack
    :navigate  - most common way to navigate to the next screen
    :setParams - used to change the params for the current screen}"
  [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation]
      [:> View {:style {:flex           1
                        :alignItems     "center"
                        :justifyContent "center"}}
       [:> Button {:style   {:fontSize 17}
                   :onPress #(navigate "Modal")
                   :title   "Modal Me!"}]
       [:> Button {:style   {:fontSize 17}
                   :onPress #(goBack)
                   :title   "Go Back!"}]
       [:> Button {:style   {:fontSize 17}
                   :onPress #(navigate "Placeholder")}]])))
```

And then you can create stacks and screens for them like this:
```clojure
(def HomeStack (stack-navigator {:Home {:screen (stack-screen home {:title "Home"})}}))

```

And then you can just render that Stack like a normal reagent component:

```clojure
(def app-root []
  (fn []
    [:> HomeStack {}]))

(defn init []
  (.registerComponent AppRegistry "UIExplorer" #(r/reactify-component app-root)))
```


TODO: Create an example


## Usage for re-frame

Pretty much the same as the Reagent version, but you can store the routing state in re-frame's app-db (which is great for serializing state to AsyncStorage).  Also, dispatch routing changes from anywhere. 

See the examples folder.

## TODO

- Finish spec'ing all functions and parameters (I think I've got most of them)
- Reagent example
- Rum example
- Om-Next example


## License

Copyright © 2017 Sean Tempesta

Distributed under the MIT license.