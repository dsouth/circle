(ns ide.core)
(import
 '(javax.swing JFrame JComponent)
 '(java.awt Color Font RenderingHints)
 '(java.awt.event KeyEvent KeyListener))

;; cursor is line number and character on line
;; e.g. [0 1] is after the first character on the first line
(def cursor (ref [0 0]))
;; buffer is a vector of line vectors
(def buffer (ref [[]]))

(defn delete []
  (let [end (- (count (@buffer (@cursor 0))) 1)
        line-number (@cursor 0)
        altered (subvec (@buffer line-number) 0 end)]
    (dosync
     (alter buffer assoc line-number altered))))

(defn add-char [event]
  (let [c (.getKeyChar event)
        line-number (@cursor 0)
        altered (conj (@buffer line-number) c)]
    (if (Character/isDefined c)
      (dosync
       (alter buffer assoc line-number altered)))))

(declare editor)

(defn key-typed [event]
  (.repaint editor))

(defn key-pressed [event]
  (if (= KeyEvent/VK_BACK_SPACE (.getKeyCode event))
    (delete)
    (add-char event)))

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
        s (apply str (@buffer (@cursor 0)))
        bounds (.getStringBounds font s frc)
        y (int (Math/ceil (.getHeight bounds)))]
    (.drawString g s 0 y)))

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
