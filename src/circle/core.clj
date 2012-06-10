(ns circle.core
  (:require [circle.config :as config]
            [circle.dispatch :as dispatch]))

(defn main []
  (config/config)
  (dispatch/fire :show-frame nil))
