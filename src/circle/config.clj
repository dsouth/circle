(ns circle.config
  (:require [circle.dispatch :as dispatch]
            [circle.edit :as edit]
            [circle.file :as file]
            [circle.gui :as gui]
            [circle.navigation :as navigation]
            [circle.state :as state]))

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

(defn- state-config []
  (dispatch/add-reactor :state-load-buffer state/load-buffer)
  (dispatch/add-reactor :state-delete-line state/delete-line))

(defn- gui-config []
  (dispatch/add-reactor :gui-load-file gui/load-file))

(defn- file-config []
  (dispatch/add-reactor :file-load-buffer file/load-buffer))

(defn config []
  (navigation-config)
  (edit-config)
  (state-config)
  (gui-config)
  (file-config))
