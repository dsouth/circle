(ns circle.event
  (:require [circle.edit :as edit]
            [circle.file :as file]
            [circle.navigation :as navigation])
  (:import (java.awt FileDialog)
           (java.awt.event KeyEvent KeyListener)))

(def key-map {KeyEvent/VK_BACK_SPACE edit/delete
              KeyEvent/VK_LEFT       navigation/cursor-backward
              KeyEvent/VK_RIGHT      navigation/cursor-forward
              KeyEvent/VK_UP         navigation/cursor-up
              KeyEvent/VK_DOWN       navigation/cursor-down})

(defn set-frame [f]
  (def frame f))

(defn gui-load []
  (let [jfc (FileDialog. frame "Load..." FileDialog/LOAD)]
    (.setVisible jfc true)
    (let [result (.getFile jfc)
          file-dir (.getDirectory jfc)
          load-src (str file-dir result)]
      (file/load-buffer load-src))))

(def meta-map {KeyEvent/VK_L gui-load})

(def modifier-map {0 key-map, KeyEvent/META_MASK meta-map})

(defn bad-kludge [e]
  (def editor e))

(defn key-typed [event]
  (.repaint editor))

;; TODO possible to do args [] and remove arity???
(defn do-with-repaint
  ([f]
     (f)
     (.repaint editor))
  ([f x]
     (f x)
     (.repaint editor)))

(defn key-pressed [event]
  (let [m (modifier-map (.getModifiers event))]
    (when m
      (let [f (m (.getKeyCode event))]
        (if f
          (apply do-with-repaint [f])
          (when (= m key-map)
            (do-with-repaint edit/add-char (.getKeyChar event))))
        ;; Don't really think should always consume event... :/
        (.consume event)))))

(defn key-released [event])

(def keylistener
  (proxy [KeyListener] []
    (keyTyped [event]
      (key-typed event))
    (keyPressed [event]
      (key-pressed event))
    (keyReleased [event]
      (key-released event))))
