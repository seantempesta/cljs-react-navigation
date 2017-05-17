(ns uiexplorer.scenes.login
  (:require [uiexplorer.react-requires :refer [ActivityIndicator Platform Button TouchableOpacity Ionicons InteractionManager View ScrollView Text TouchableHighlight]]
            [cljs-react-navigation.re-frame :refer [stack-navigator tab-navigator stack-screen tab-screen router]]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]))

(def static-navigationOptions {:headerTitle "Login"
                               :headerRight (fn []
                                              [:> Button {:title   "Sign In"
                                                          :onPress #(dispatch [:login])}])})

(defn dynamic-navigationOptions [{:keys [navigation] :as props}]
  (let [navigate (:navigate navigation)]
    {:headerTitle "Login"
     :headerRight (fn []
                    [:> Button {:title   "Sign In"
                                :onPress #(navigate "Loading")}])}))

(defn login1 [{:keys [screenProps navigation] :as props}]
  (let [navigate (:navigate navigation)]
    (fn [props]
      [:> View {:style {:flex           1
                        :alignItems     "center"
                        :justifyContent "center"}}
       [:> Button {:style   {:fontSize 17}
                   :onPress #(do (dispatch [:login])
                                 (navigate "Loading"))
                   :title   "Click to Login!"}]])))

(defn loading [props]
  (fn [props]
    [:> View {:style {:flex            1
                      :backgroundColor "#333333"
                      :alignItems      "center"
                      :justifyContent  "center"}}
     [:> ActivityIndicator
      {:animating true
       :style     {:alignItems     "center"
                   :justifyContent "center"
                   :height         80}
       :size      "large"}]]))