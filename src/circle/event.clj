(ns circle.event
  (:require [circle.dispatch :as dispatch])
  (:import (java.awt.event KeyEvent KeyListener)))

(def key-map {KeyEvent/VK_BACK_SPACE :key-backspace
              KeyEvent/VK_LEFT       :key-left
              KeyEvent/VK_RIGHT      :key-right
              KeyEvent/VK_UP         :key-up
              KeyEvent/VK_DOWN       :key-down})

(def meta-map {KeyEvent/VK_L :gui-load-file})

(def modifier-map {0 key-map, KeyEvent/META_MASK meta-map})

(defn key-pressed [event]
  (let [m (modifier-map (.getModifiers event))]
    (when m
      (let [f (m (.getKeyCode event))]
        (if f
          (do
            (dispatch/fire f nil)
            (.consume event))
          (when (= m key-map)
            (dispatch/fire :key-typed (.getKeyChar event))
            (.consume event)))))))

(defn key-released [event])
(defn key-typed [event])

(def keylistener
  (proxy [KeyListener] []
    (keyTyped [event]
      (key-typed event))
    (keyPressed [event]
      (key-pressed event))
    (keyReleased [event]
      (key-released event))))
