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
    (edit/add-char (.getKeyChar event))))

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

(defn get-cursor-x [frc s]
  (if (= 0 (count s))
    0
    (let [glyph-vector (.createGlyphVector font frc s)
          cursor-index (edit/get-horizontal-cursor-position)
          bounding-shape (.getGlyphLogicalBounds glyph-vector cursor-index)
          bounding-rect (.getBounds bounding-shape)]
      (int (+ (.getX bounding-rect) (.getWidth bounding-rect))))))

(defn editor-paint [g]
  (let [frc (.getFontRenderContext g)
        s (edit/get-line 0)
        bounds (.getStringBounds font s frc)
        line-height (int (Math/ceil (.getHeight bounds)))]
    (let [n (edit/line-count)]
      (dotimes [i n]
        (.drawString g (edit/get-line i) 0 (* (+ 1 i) line-height))))
    (let [i (edit/cursor-line)
          top (* i line-height)
          bottom (+ top line-height)
          cursor-x (get-cursor-x frc (edit/get-line i))]
      (.drawLine g cursor-x top cursor-x bottom))))

(def editor
  (proxy [JComponent] []
    (paint [g]
      (editor-paint g))))

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
