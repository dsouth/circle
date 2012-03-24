(ns circle.core
  (:require [circle.edit :as edit])
  (:import (javax.swing JFrame JComponent JScrollPane SwingUtilities)
           (java.awt Color Dimension Font RenderingHints)
           (java.awt.event KeyEvent KeyListener)))

(declare editor)

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
     (do-with-repaint edit/cursor-backward)

     (= KeyEvent/VK_RIGHT code)
     (do-with-repaint edit/cursor-forward)

     (= KeyEvent/VK_UP code)
     (do-with-repaint edit/cursor-up)

     (= KeyEvent/VK_DOWN code)
     (do-with-repaint edit/cursor-down)

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

(defn get-bounding-rect
  ([font frc]
     (get-bounding-rect font frc "." 0))
  ([font frc s i]
       (-> (.createGlyphVector font frc s)
           (.getGlyphLogicalBounds i)
           .getBounds)))

(defn get-cursor-x [font frc s]
  "Returns the pixel x coordinate for the cursor. Assumes fixed width font.
Also responsible for keeping the cursor in the viewport for the scroll pane."
    (let [bounding-rect (get-bounding-rect font frc)
          y (* -1 (.getY bounding-rect))]
      (.translate bounding-rect 0 (+ y (* (dec (edit/get-cursor-line)) (.getHeight bounding-rect))))
      (.scrollRectToVisible editor bounding-rect)
      (int (+ (.getX bounding-rect)
              (* (edit/get-horizontal-cursor-position) (.getWidth bounding-rect))))))

(defn baseline
  "Given the index, i, of a line of text, its height and descent
returns the baseline for drwaing the line"
  [i line-height descent]
  (- (* (+ 1 i)
        line-height)
     descent))

(defn set-preferred-size [font frc]
  (let [bounding-rect (get-bounding-rect font frc)
        height (* (edit/line-count) (.getHeight bounding-rect))
        width (* (edit/longest-line-count) (.getWidth bounding-rect))
        size (Dimension. width height)]
    (.setPreferredSize editor size)))

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
      (.drawLine g cursor-x top cursor-x bottom))
    (set-preferred-size font frc)
    (.revalidate editor)))

(defn main []
  (def editor (proxy [JComponent] []
                (paintComponent [g]
                  (proxy-super paintComponent g)
                  (editor-paint g))))
  (def frame (JFrame. "Circle"))
  (.setFont editor (Font. "Menlo" Font/PLAIN 24))
  (.addKeyListener editor keylistener)
  (.setDefaultCloseOperation frame JFrame/DISPOSE_ON_CLOSE)
  (let [jsp (JScrollPane. editor)]
    (.add frame jsp)
    (.setPreferredSize jsp (Dimension. 800 600)))
  (comment (.add frame editor))
  (.pack frame)
  (.requestFocus editor) ;; perhaps on an expose listener? Or a focus manager???
  (.setVisible frame true))

(defn show []
  (SwingUtilities/invokeLater main))
