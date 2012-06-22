(ns circle.gui
  (:require [circle.dispatch :as dispatch])
  (:import (java.awt Color Dimension FileDialog Font RenderingHints)
           (java.awt.event KeyEvent)
           (java.io FilenameFilter)
           (javax.swing JFrame JComponent SwingUtilities)))

(def screen-delta 0)
(defn set-frame [f]
  (def frame f))

(def file-filter (proxy [FilenameFilter] []
              (accept [_ f]
                (.endsWith f ".clj"))))

(defn load-source-file []
    (let [jfc (FileDialog. frame "Load..." FileDialog/LOAD)]
    (.setFilenameFilter jfc file-filter)
    (.setVisible jfc true)
    (let [result (.getFile jfc)
          file-dir (.getDirectory jfc)
          load-src (str file-dir result)]
      (when result
        (dispatch/fire :file-load-buffer load-src)))))

(defn key-event [{code :code modifier :modifier event :event}]
  (when (and (= code KeyEvent/VK_L)
             (= modifier KeyEvent/META_MASK))
    (load-source-file)))

(defn get-bounding-rect
  ([font frc]
     (get-bounding-rect font frc "." 0))
  ([font frc s i]
       (-> (.createGlyphVector font frc s)
           (.getGlyphLogicalBounds i)
           .getBounds)))

(declare editor)

(defn update-screen-delta []
  (let [d (.getSize editor)]
    (println "---" d "---")))

(defn get-cursor-x
  "Returns the pixel x coordinate for the cursor. Assumes fixed width font.
Also responsible for keeping the cursor in the viewport for the scroll pane."
  [font frc s]
  (update-screen-delta)
  (let [bounding-rect (get-bounding-rect font frc)
        y (* -1 (.getY bounding-rect))
        x (* (dispatch/receive :state-get-cursor-x) (.getWidth bounding-rect))]
    (.translate bounding-rect x (+ y (* (dec (dispatch/receive :state-get-cursor-line))
                                        (.getHeight bounding-rect))))
    (int (.getX bounding-rect))))

(defn baseline
  "Given the index, i, of a line of text, its height and descent
returns the baseline for drawing the line"
  [i line-height descent]
  (- (* (+ 1 i)
        line-height)
     descent screen-delta))

(defn set-preferred-size [font frc]
  (let [bounding-rect (get-bounding-rect font frc)
        height (* (dispatch/receive :state-get-line-count) (.getHeight bounding-rect))
        width (+ 1 (* (dispatch/receive :state-get-longest-line-count) (.getWidth bounding-rect)))
        size (Dimension. width height)]
    (.setPreferredSize editor size)))

(defn editor-paint
  "Paint the contents of the editor given the Grapics g"
  [g]
  (let [font-metrics (.getFontMetrics g)
        frc (.getFontRenderContext g)
        s (dispatch/receive :state-get-line 0)
        font (.getFont g)
        bounds (.getStringBounds font s frc)
        line-height (int (Math/ceil (.getHeight bounds)))]
    (let [n (dispatch/receive :state-get-line-count)
          descent (.getDescent font-metrics)]
      (dotimes [i n]
        (.drawString g (dispatch/receive :state-get-line i) 0 (baseline i line-height descent))))
    (let [i (dispatch/receive :state-get-cursor-line)
          top (* i line-height)
          bottom (+ top line-height)
          cursor-x (get-cursor-x font frc (dispatch/receive :state-get-line i))]
      (.drawLine g cursor-x top cursor-x bottom))
    (set-preferred-size font frc)
    (.revalidate editor)))

(defn show [_]
  (def editor (proxy [JComponent] []
                (paintComponent [g]
                  (proxy-super paintComponent g)
                  (editor-paint g))))
  (def frame (JFrame. "Circle"))
  (set-frame frame)
  (.setFont editor (Font. "Menlo" Font/PLAIN 24))
  (.addKeyListener editor (dispatch/receive :key-listener))
  (.setDefaultCloseOperation frame JFrame/DISPOSE_ON_CLOSE)
  (.add frame editor)
  (.setPreferredSize editor (Dimension. 800 600))
  (.pack frame)
  (.requestFocus editor) ;; perhaps on an expose listener? Or a focus manager???
  (.setVisible frame true))

; find a way to move this to config without cyclic dependency :(
(dispatch/add-reactor :repaint (fn [_] (.repaint editor)))

#_(defn show []
    (SwingUtilities/invokeAndWait main))
