(ns cljs-react-navigation.re-frame
  (:require [cljs-react-navigation.base :as base]
            [cljs-react-navigation.reagent :as reagent]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync reg-event-db trim-v reg-sub]]))

(def ref-getStateForAction (atom nil))                      ;; HACK

(reg-event-db
  ::swap-routing-state
  [trim-v]
  (fn [app-db [new-routes]]
    (assoc app-db :routing new-routes)))

(reg-event-db
  ::dispatch
  [trim-v]
  (fn [app-db [dispatch-args]]
    (let [routing-state (get app-db :routing)
          type (aget dispatch-args "type")
          action-fn (get reagent/NavigationActionsMap type)
          action (action-fn dispatch-args)
          new-state (@ref-getStateForAction action routing-state)]
      (assoc app-db :routing new-state))))

(reg-event-db
  ::navigate
  [trim-v]
  (fn [app-db [routeName params]]
    (let [routing-state (get app-db :routing)
          action-fn (get reagent/NavigationActionsMap "Navigation/NAVIGATE")
          action (action-fn #js {:routeName routeName :params params})
          new-state (@ref-getStateForAction action routing-state)]
      (assoc app-db :routing new-state))))


(reg-event-db
  ::goBack
  [trim-v]
  (fn [app-db [routeName]]
    (let [routing-state (get app-db :routing)
          action-fn (get reagent/NavigationActionsMap "Navigation/BACK")
          action (action-fn #js {:routeName routeName})
          new-state (@ref-getStateForAction action routing-state)]
      (assoc app-db :routing new-state))))

(reg-sub
  ::routing-state
  (fn [app-db]
    (get-in app-db [:routing])))


;; API
(def stack-screen reagent/stack-screen)
(def tab-screen reagent/tab-screen)
(def drawer-component reagent/drawer-component)
(def stack-navigator reagent/stack-navigator)
(def tab-navigator reagent/tab-navigator)
(def drawer-navigator reagent/drawer-navigator)
(def switch-navigator reagent/switch-navigator)

(def init-state
  (fn [main key]
    (-> main
        .-router
        (as-> router
            (.getStateForAction router
                                (.getActionForPathAndParams router (name key)))))))

(def nil-fn (fn [_]))

(defn router [{:keys [root-router init-route-name add-listener]
               :or {add-listener nil-fn init-route-name :start-route}
               :as props}]
  (let [routing-sub (subscribe [::routing-state])
        getStateForAction (aget root-router "router" "getStateForAction")]
    (reset! ref-getStateForAction getStateForAction)
    (fn [props]
      (let [routing-state (or @routing-sub
                              (init-state root-router init-route-name))]
        [:> root-router {:navigation
                         (base/addNavigationHelpers
                          (clj->js {:state    routing-state
                                    :addListener add-listener
                                    :dispatch (fn [action]
                                                (let [next-state (getStateForAction action routing-state)]
                                                  (dispatch [::swap-routing-state next-state])))}))}]))))
