(ns uiexplorer.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [uiexplorer.react-requires :refer [Expo]]
            [uiexplorer.routing :as routing]
            [uiexplorer.events]
            [uiexplorer.subs]))

(def app-root routing/app-root)

(defn init []
      (aset js/console "disableYellowBox" true)
  (dispatch-sync [:initialize-db])
  (.registerRootComponent Expo (r/reactify-component routing/app-root))
      ;; (.registerComponent AppRegistry "UIExplorer" #(r/reactify-component routing/app-root))
  )
