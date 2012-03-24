(ns circle.events
  (:require [circle.edit :as edit]
            [circle.core :as core])
  (:import (java.awt.event KeyEvent KeyListener)))

(defn key-typed [event]
  (.repaint core/editor))

(defn do-with-repaint
  ([f]
     (f)
     (.repaint core/editor))
  ([f x]
     (f x)
     (.repaint core/editor)))

(defn key-pressed [event]
  (let [code (.getKeyCode event)]
    (cond
     (= KeyEvent/VK_BACK_SPACE code)
     (do-with-repaint edit/delete)

     (= KeyEvent/VK_LEFT code)
     (do-with-repaint edit/cursor-backward)

     (= KeyEvent/VK_RIGHT code)
     (do-with-repaint edit/cursor-forward)

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
