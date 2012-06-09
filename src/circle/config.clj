(ns circle.config
  (:require [circle.dispatch :as dispatch]
            [circle.edit :as edit]
            [circle.event :as event]
            [circle.file :as file]
            [circle.gui :as gui]
            [circle.navigation :as navigation]
            [circle.state :as state]))

(defn- reactor [k f]
  (dispatch/add-reactor k f))

(defn- navigation-config []
  (reactor :key-event navigation/key-event))

(defn- edit-config []
  (reactor :key-event edit/key-event))

(defn- state-config []
  (reactor :state-load-buffer state/load-buffer)
  (reactor :state-delete-line state/delete-line)
  (reactor :state-delete-char-before-cursor state/delete-char-before-cursor)
  (reactor :state-move-cursor state/move-cursor)
  (reactor :state-modify-buffer state/modify-buffer)
  (reactor :state-modify-buffer-line state/modify-buffer-line)
  (dispatch/add-producer :state-get-cursor-line #(identity @state/cursor-line))
  (dispatch/add-producer :state-get-cursor-x #(identity @state/cursor-x))
  (dispatch/add-producer :state-get-buffer #(identity @state/buffer))
  (dispatch/add-producer :state-get-line-count state/line-count)
  (dispatch/add-producer :state-get-longest-line-count state/longest-line-count)
  (dispatch/add-producer :state-get-line state/get-line)
  (dispatch/add-producer :state-get-text-from state/get-text-from))

(defn- gui-config []
  (reactor :key-event gui/key-event)
  (reactor :set-frame gui/set-frame))

(defn- file-config []
  (reactor :file-load-buffer file/load-buffer))

(defn- event-config []
  (dispatch/add-producer :key-listener #(identity event/keylistener)))

;;; would really be nice if each namespace defined a config function and then
;;; that function was invoked for each namespace loaded?
(defn config []
  (navigation-config)
  (edit-config)
  (state-config)
  (gui-config)
  (file-config)
  (event-config))
