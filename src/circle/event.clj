(ns circle.event
  (:require [circle.edit :as edit])
  (:import (java.awt.event KeyEvent KeyListener)))

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
  (let [code (.getKeyCode event)]
    (cond
     (= KeyEvent/VK_BACK_SPACE code)
     (do-with-repaint edit/delete)

     (= KeyEvent/VK_LEFT code)
     (do
       (do-with-repaint edit/cursor-backward)
       (.consume event))

     (= KeyEvent/VK_RIGHT code)
     (do
       (do-with-repaint edit/cursor-forward)
       (.consume event))

     (= KeyEvent/VK_UP code)
     (do
       (do-with-repaint edit/cursor-up)
       (.consume event))

     (= KeyEvent/VK_DOWN code)
     (do
       (do-with-repaint edit/cursor-down)
       (.consume event))

     :otherwise
     (do-with-repaint edit/add-char (.getKeyChar event)))))

(defn key-released [event])

(def keylistener
  (proxy [KeyListener] []
    (keyTyped [event]
      (key-typed event))
    (keyPressed [event]
      (key-pressed event))
    (keyReleased [event]
      (key-released event))))
