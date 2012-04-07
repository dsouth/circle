(ns circle.config
  (:require [circle.dispatch :as dispatch]
            [circle.edit :as edit]
            [circle.event :as event]
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
  (dispatch/add-reactor :state-delete-line state/delete-line)
  (dispatch/add-reactor :state-delete-char-before-cursor state/delete-char-before-cursor)
  (dispatch/add-reactor :state-move-cursor state/move-cursor)
  (dispatch/add-reactor :state-modify-buffer state/modify-buffer)
  (dispatch/add-reactor :state-modify-buffer-line state/modify-buffer-line)
  (dispatch/add-askor :state-get-cursor-line #(identity @state/cursor-line))
  (dispatch/add-askor :state-get-cursor-x #(identity @state/cursor-x))
  (dispatch/add-askor :state-get-buffer #(identity @state/buffer))
  (dispatch/add-askor :state-get-line-count state/line-count)
  (dispatch/add-askor :state-get-longest-line-count state/longest-line-count))

(defn- gui-config []
  (dispatch/add-reactor :gui-load-file gui/load-source-file))

(defn- file-config []
  (dispatch/add-reactor :file-load-buffer file/load-buffer))

(defn- event-config []
  (dispatch/add-askor :key-listener #(identity event/keylistener)))

;;; would really be nice if each namespace defined a config function and then
;;; that function was invoked for each namespace loaded?
(defn config []
  (navigation-config)
  (edit-config)
  (state-config)
  (gui-config)
  (file-config)
  (event-config))
