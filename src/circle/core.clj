(ns circle.core
  (:require [circle.config :as config]
            [circle.gui :as gui]
            [circle.state :as state]
            [circle.dispatch :as dispatch])
  (:import (javax.swing JFrame JComponent JScrollPane SwingUtilities)
           (java.awt Color Dimension Font RenderingHints)))

(defn get-bounding-rect
  ([font frc]
     (get-bounding-rect font frc "." 0))
  ([font frc s i]
       (-> (.createGlyphVector font frc s)
           (.getGlyphLogicalBounds i)
           .getBounds)))

(declare editor)

(defn get-cursor-x [font frc s]
  "Returns the pixel x coordinate for the cursor. Assumes fixed width font.
Also responsible for keeping the cursor in the viewport for the scroll pane."
    (let [bounding-rect (get-bounding-rect font frc)
          y (* -1 (.getY bounding-rect))]
      (.translate bounding-rect 0 (+ y (* (dec (dispatch/receive :state-get-cursor-line))
                                          (.getHeight bounding-rect))))
      (.scrollRectToVisible editor bounding-rect)
      (int (+ (.getX bounding-rect)
              (* (dispatch/receive :state-get-cursor-x) (.getWidth bounding-rect))))))

(defn baseline
  "Given the index, i, of a line of text, its height and descent
returns the baseline for drwaing the line"
  [i line-height descent]
  (- (* (+ 1 i)
        line-height)
     descent))

(defn set-preferred-size [font frc]
  (let [bounding-rect (get-bounding-rect font frc)
        height (* (state/line-count) (.getHeight bounding-rect))
        width (* (state/longest-line-count) (.getWidth bounding-rect))
        size (Dimension. width height)]
    (.setPreferredSize editor size)))

(defn editor-paint
  "Paint the contents of the editor given the Grapics g"
  [g]
  (let [font-metrics (.getFontMetrics g)
        frc (.getFontRenderContext g)
        s (state/get-line 0)
        font (.getFont g)
        bounds (.getStringBounds font s frc)
        line-height (int (Math/ceil (.getHeight bounds)))]
    (let [n (state/line-count)
          descent (.getDescent font-metrics)]
      (dotimes [i n]
        (.drawString g (state/get-line i) 0 (baseline i line-height descent))))
    (let [i (state/get-cursor-line)
          top (* i line-height)
          bottom (+ top line-height)
          cursor-x (get-cursor-x font frc (state/get-line i))]
      (.drawLine g cursor-x top cursor-x bottom))
    (set-preferred-size font frc)
    (.revalidate editor)))

(defn main []
  (config/config)
  (def editor (proxy [JComponent] []
                (paintComponent [g]
                  (proxy-super paintComponent g)
                  (editor-paint g))))
  (def frame (JFrame. "Circle"))
  (gui/set-frame frame)
  (.setFont editor (Font. "Menlo" Font/PLAIN 24))
  (.addKeyListener editor (dispatch/receive :key-listener))
  (.setDefaultCloseOperation frame JFrame/DISPOSE_ON_CLOSE)
  (let [jsp (JScrollPane. editor)]
    (.add frame jsp)
    (.setPreferredSize jsp (Dimension. 800 600)))
  (comment (.add frame editor))
  (.pack frame)
  (.requestFocus editor) ;; perhaps on an expose listener? Or a focus manager???
  (.setVisible frame true))

; find a way to move this to config without cyclic dependency :(
(dispatch/add-reactor :repaint (fn [_] (.repaint editor)))

(defn show []
  (SwingUtilities/invokeLater main))
