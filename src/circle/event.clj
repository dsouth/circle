(ns circle.event
  (:require [circle.edit :as edit]
            [circle.navigation :as navigation])
  (:import (java.awt.event KeyEvent KeyListener)))

(def key-map {KeyEvent/VK_BACK_SPACE edit/delete
              KeyEvent/VK_LEFT       navigation/cursor-backward
              KeyEvent/VK_RIGHT      navigation/cursor-forward
              KeyEvent/VK_UP         navigation/cursor-up
              KeyEvent/VK_DOWN       navigation/cursor-down})

(defn bad-kludge [e]
  (def editor e))

(defn key-typed [event]
  (.repaint editor))

(defn do-with-repaint
  ([f]
     (f)
     (.repaint editor))
  ([f x]
     (f x)
     (.repaint editor)))

(defn key-pressed [event]
  (let [f (key-map (.getKeyCode event))]
    (if f
      (apply do-with-repaint [f])
      (do-with-repaint edit/add-char (.getKeyChar event)))
    (.consume event)))

(defn key-released [event])

(def keylistener
  (proxy [KeyListener] []
    (keyTyped [event]
      (key-typed event))
    (keyPressed [event]
      (key-pressed event))
    (keyReleased [event]
      (key-released event))))
