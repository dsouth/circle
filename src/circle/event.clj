(ns circle.event
  (:require [circle.dispatch :as dispatch])
  (:import (java.awt.event KeyEvent KeyListener)))

; Map from Java Swing events to applicaiton events
(def key-map (ref {KeyEvent/VK_BACK_SPACE :key-backspace
                   KeyEvent/VK_LEFT       :key-left
                   KeyEvent/VK_RIGHT      :key-right
                   KeyEvent/VK_UP         :key-up
                   KeyEvent/VK_DOWN       :key-down}))

(def meta-map (ref {KeyEvent/VK_L :gui-load-file}))

(defn- add-to-map [m k e]
  (dosync
   (alter m assoc k e)))

(defn add-to-key-map
  "Adds an event e to the key-map with key k."
  [k e]
  (add-to-map key-map k e))

(defn add-to-meta-map
  "Adds an event e to the meta-map with key k."
  [k e]
  (add-to-map meta-map k e))

; meta map for modification key to Java Swing to application event map
(def modifier-map {0 key-map,
                   KeyEvent/META_MASK meta-map})

(defn key-pressed [event]
  (let [modifier (.getModifiers event)]
    (if (modifier-map modifier)
      (let [m @(modifier-map modifier)]
        (println "m is" m)
        (if m
          (let [f (m (.getKeyCode event))]
            (println "f is" f)
            (if f
              (do
                (dispatch/fire f nil)
                (.consume event))
              ;; Normal key presses don't have a function. Maybe they should??? :|
              (when (= m @key-map)
                (dispatch/fire :key-typed (.getKeyChar event))
                (.consume event))))))
      (when (= KeyEvent/SHIFT_MASK)
        (dispatch/fire :key-typed (.getKeyChar event))))
    (dispatch/fire :key-event {:key (.getKeyChar event) :modifier modifier})))

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
