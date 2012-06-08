(ns circle.event
  (:require [circle.dispatch :as dispatch])
  (:import (java.awt.event KeyEvent KeyListener)))

; Map from Java Swing events to applicaiton events
(def key-map (ref {KeyEvent/VK_BACK_SPACE :key-backspace
                   KeyEvent/VK_LEFT       :key-left
                   KeyEvent/VK_RIGHT      :key-right
                   KeyEvent/VK_UP         :key-up
                   KeyEvent/VK_DOWN       :key-down}))

; meta map for modification key to Java Swing to application event map
(def modifier-map {0 key-map})

(defn key-pressed [event]
  (let [modifier (.getModifiers event)]
    (dispatch/fire :key-event {:key (let [key (.getKeyChar event)]
                                      (if (= key KeyEvent/CHAR_UNDEFINED)
                                        nil
                                        key))
                               :modifier modifier
                               :event event})
    (when (modifier-map modifier)
      (let [m @(modifier-map modifier)]
      (if m
        (let [f (m (.getKeyCode event))]
          (if f
            (do
              (dispatch/fire f nil)
              (.consume event)))))))))

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
