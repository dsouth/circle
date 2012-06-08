(ns circle.event
  (:require [circle.dispatch :as dispatch])
  (:import (java.awt.event KeyEvent KeyListener)))

; Map from Java Swing events to applicaiton events
(def key-map (ref {KeyEvent/VK_BACK_SPACE :key-backspace}))

; meta map for modification key to Java Swing to application event map
(def modifier-map {0 key-map})

(defn- event-map [e]
  {:key (.getKeyChar e)
   :code (.getKeyCode e)
   :modifier (.getModifiers e)
   :event e})

(defn key-pressed [event]
  (let [m (event-map event)]
    (dispatch/fire :key-event m)
    (let [modifier (:modifier m)]
      (when (modifier-map modifier)
        (let [m @(modifier-map modifier)]
          (if m
            (let [f (m (.getKeyCode event))]
              (if f
                (do
                  (dispatch/fire f nil)
                  (.consume event))))))))))

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
