(ns uiexplorer.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [uiexplorer.react-requires :refer [AppRegistry]]
            [uiexplorer.routing :as routing]
            [uiexplorer.events]
            [uiexplorer.subs]))

(def app-root routing/app-root)

(defn init []
      (aset js/console "disableYellowBox" true)
      (dispatch-sync [:initialize-db])
      (.registerComponent AppRegistry "UIExplorer" #(r/reactify-component routing/app-root)))