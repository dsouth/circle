(ns circle.core
  (:require [circle.edit :as edit])
  (:import (javax.swing JFrame JComponent)
           (java.awt Color Font RenderingHints)
           (java.awt.event KeyEvent KeyListener)))

(declare editor)

(defn key-typed [event]
  (.repaint editor))

(defn key-pressed [event]
  (let [code (.getKeyCode event)]
    (cond
     (= KeyEvent/VK_BACK_SPACE code)
     (edit/delete)

     (= KeyEvent/VK_LEFT code)
     (do
       (edit/cursor-backword)
       (.repaint editor))

     (= KeyEvent/VK_RIGHT code)
     (println "RIGHT")

     (= KeyEvent/VK_UP code)
     (println "UP")

     (= KeyEvent/VK_DOWN code)
     (println "DOWN")

     :otherwise
     (edit/add-char (.getKeyChar event))))
                                        ; probably inefficient, but this will do for now
  (.repaint editor))

(defn key-released [event])

(def keylistener
  (proxy [KeyListener] []
    (keyTyped [event]
      (key-typed event))
    (keyPressed [event]
      (key-pressed event))
    (keyReleased [event]
      (key-released event))))

(defn get-cursor-x [font frc s]
  (if (or (= 0 (count s))
          (= 0 (edit/get-horizontal-cursor-position)))
    0
    (let [cursor-index (dec (edit/get-horizontal-cursor-position))
          bounding-rect (-> (.createGlyphVector font frc s)
                            (.getGlyphLogicalBounds cursor-index)
                            .getBounds)]
      (int (+ (.getX bounding-rect)
              (.getWidth bounding-rect))))))

(defn baseline
  "Given the index, i, of a line of text, its height and descent
returns the baseline for drwaing the line"
  [i line-height descent]
  (- (* (+ 1 i)
        line-height)
     descent))

(defn editor-paint
  "Paint the contents of the editor given the Grapics g"
  [g]
  (let [font-metrics (.getFontMetrics g)
        frc (.getFontRenderContext g)
        s (edit/get-line 0)
        font (.getFont g)
        bounds (.getStringBounds font s frc)
        line-height (int (Math/ceil (.getHeight bounds)))]
    (let [n (edit/line-count)
          descent (.getDescent font-metrics)]
      (dotimes [i n]
        (.drawString g (edit/get-line i) 0 (baseline i line-height descent))))
    (let [i (edit/get-cursor-line)
          top (* i line-height)
          bottom (+ top line-height)
          cursor-x (get-cursor-x font frc (edit/get-line i))]
      (.drawLine g cursor-x top cursor-x bottom))))

(def editor
  (proxy [JComponent] []
    (paint [g]
      (editor-paint g))))

(.setFont editor (Font. "Menlo" Font/PLAIN 24))

(def panel editor)
(.addKeyListener panel keylistener)

(def frame (JFrame. "Circle"))
(.setDefaultCloseOperation frame JFrame/DISPOSE_ON_CLOSE)
(.add frame panel)
(.pack frame)
(.requestFocus panel) ;; perhaps on an expose listener? Or a focus manager???

(defn show []
  (.setVisible frame true))
