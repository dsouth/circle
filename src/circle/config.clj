(ns circle.config
  (:require [circle.dispatch :as dispatch]
            [circle.edit :as edit]
            [circle.navigation :as navigation]))

(defn- navigation-config []
  (dispatch/add-reactor :key-right (fn [_]
                                     (navigation/cursor-move navigation/forward)
                                     (dispatch/fire :repaint nil)))
  (dispatch/add-reactor :key-left  (fn [_]
                                     (navigation/cursor-move navigation/backward)
                                     (dispatch/fire :repaint nil)))
  (dispatch/add-reactor :key-up    (fn [_]
                                     (navigation/cursor-move navigation/up)
                                     (dispatch/fire :repaint nil)))
  (dispatch/add-reactor :key-down  (fn [_]
                                     (navigation/cursor-move navigation/down)
                                     (dispatch/fire :repaint nil))))

(defn- edit-config []
  (dispatch/add-reactor :key-backspace edit/delete)
  (dispatch/add-reactor :key-typed     edit/add-char))

(defn config []
  (navigation-config)
  (edit-config))
