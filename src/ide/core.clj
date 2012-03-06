(ns ide.core)
(import
 '(javax.swing JFrame JComponent)
 '(java.awt Color Font RenderingHints)
 '(java.awt.event KeyEvent KeyListener))

(def buffer (ref []))

(defn delete []
  (let [end (- (count @buffer) 1)]
    (dosync
     (alter buffer subvec 0 end))))

(defn add-char [event]
  (let [c (.getKeyChar event)]
    (if (Character/isDefined c)
      (dosync
       (alter buffer conj c)))))

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

(defn editor-paint [g]
  (.setColor g Color/BLACK)
  (.drawString g (apply str @buffer) 100 100))

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
