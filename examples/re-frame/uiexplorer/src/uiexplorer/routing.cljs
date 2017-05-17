(ns uiexplorer.routing
  (:require [uiexplorer.react-requires :refer [Platform Button TouchableOpacity Ionicons InteractionManager View ScrollView Text TouchableHighlight]]
            [uiexplorer.scenes.login :as login]
            [cljs-react-navigation.re-frame :refer [stack-navigator tab-navigator stack-screen tab-screen router]]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]))

;; Colors
;;
(def darkGrey "#484848")
(def blue "#348BFE")

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
                   :onPress #(navigate "Placeholder")
                   :title   "Push Placeholder"}]
       [:> Button {:style   {:fontSize 17}
                   :onPress #(dispatch [:reset-routing-state])
                   :title   "Logout"}]])))

(defn search [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [state setParams]} navigation
          {:keys [params]} state]
      [:> View {:style {:flex           1
                        :alignItems     "center"
                        :justifyContent "center"}}
       [:> Text {} (str "Searching for " (:search params))]

       [:> Button {:style   {:fontSize 17}
                   :onPress #(setParams (clj->js {:search (rand-int 100)}))
                   :title   "Search!"}]])))

(defn placeholder [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [goBack]} navigation]
      [:> View {:style {:flex           1
                        :alignItems     "center"
                        :justifyContent "center"}}
       [:> Text {} "Nothing to see here!"]
       [:> Button {:style   {:fontSize 17}
                   :onPress #(goBack)
                   :title   "Go Back!"}]])))

(def home-tabBar {:tabBarLabel "Home"
                  :tabBarIcon  (fn [{:keys [tintColor focused] :as props}]
                                 [:> Ionicons {:name  (if focused "ios-home" "ios-home-outline")
                                               :color (if focused blue darkGrey)
                                               :size  26}])})

(def search-tabBar {:tabBarLabel "Search"
                    :tabBarIcon  (fn [{:keys [tintColor focused] :as props}]
                                   [:> Ionicons {:name  (if focused "ios-search" "ios-search-outline")
                                                 :color (if focused blue darkGrey)
                                                 :size  26}])})


(def LoginStack (stack-navigator {:Login1  {:screen (stack-screen login/login1 login/dynamic-navigationOptions)}
                                  :Loading {:screen (stack-screen login/loading {:header nil})}}))
(def HomeStack (stack-navigator {:Home        {:screen (stack-screen home {:title "Home"})}
                                 :Placeholder {:screen (stack-screen placeholder {:title "Placeholder"})}}))
(def SearchStack (stack-navigator {:Search {:screen (stack-screen search {:title "Search"})}}))

(def navbar-marginTop (if (= (aget Platform "OS") "android") 24 0))
(def Tabs (tab-navigator {:HomeTab   {:path   "home"
                                      :screen (tab-screen HomeStack home-tabBar)}
                          :SearchTab {:path   "search"
                                      :screen (tab-screen SearchStack search-tabBar)}}
                         {:tabBarOptions {:style {:marginTop navbar-marginTop}}}))

(def AllRoutesStack (stack-navigator {:Tabs       {:screen Tabs}
                                      :Modal      {:screen (stack-screen placeholder {:title "Modal"})}
                                      :LoginStack {:screen LoginStack}}
                                     {:headerMode "none"
                                      :mode       "modal"}))

(defn app-root []
  (r/create-class
    {:component-will-mount (fn []
                             (dispatch-sync [:reset-routing-state]))
     :reagent-render       (fn []
                             [router {:root-router AllRoutesStack}])}))
