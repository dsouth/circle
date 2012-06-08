(ns circle.event
  (:require [circle.dispatch :as dispatch])
  (:import (java.awt.event KeyEvent KeyListener)))

(defn- event-map [e]
  {:key (.getKeyChar e)
   :code (.getKeyCode e)
   :modifier (.getModifiers e)
   :event e})

(defn key-pressed [event]
  (dispatch/fire :key-event (event-map event)))

; Java interop stuff...
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
