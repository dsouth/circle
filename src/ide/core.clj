(ns ide.core
  (:require [ide.edit :as edit])
  (:import (javax.swing JFrame JComponent)
           (java.awt Color Font RenderingHints)
           (java.awt.event KeyEvent KeyListener)))

(declare editor)

(defn key-typed [event]
  (.repaint editor))

(defn key-pressed [event]
  (if (= KeyEvent/VK_BACK_SPACE (.getKeyCode event))
    (edit/delete)
    (edit/add-char event)))

(defn key-released [event])

(def keylistener
  (proxy [KeyListener] []
    (keyTyped [event]
      (key-typed event))
    (keyPressed [event]
      (key-pressed event))
    (keyReleased [event]
      (key-released event))))

(declare font)

(defn editor-paint [g]
  (let [frc (.getFontRenderContext g)
        s (edit/get-line)
        bounds (.getStringBounds font s frc)
        y (int (Math/ceil (.getHeight bounds)))
        glyph-vector (.createGlyphVector font frc s)
        cursor-index (- (edit/get-horizontal-cursor-position) 1)
        bounding-shape (.getGlyphLogicalBounds glyph-vector cursor-index)
        bounding-rect (.getBounds bounding-shape)
        cursor-x (int (+ (.getWidth bounding-rect) (.getX bounding-rect)))]
    (.drawString g s 0 y)
    (.drawLine g cursor-x 0 cursor-x y)))

(defn editor-update [g]
  (println "update")
  (.setColor g Color/WHITE))

(def editor
  (proxy [JComponent] []
    (paint [g]
      (editor-paint g))
    (update [g]
      (editor-update g))))

(def font (Font. "Menlo" Font/PLAIN 24))

(.setFont editor font)

(def panel editor)
(.addKeyListener panel keylistener)

(def frame (JFrame.))
(.setDefaultCloseOperation frame JFrame/DISPOSE_ON_CLOSE)
(.add frame panel)
(.pack frame)
(.requestFocus panel) ;; perhaps on an expose listener? Or a focus manager???

(defn show []
  (.setVisible frame true))
