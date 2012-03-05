(ns ide.core)
(import
 '(javax.swing JFrame JPanel)
 '(java.awt.event KeyEvent  KeyListener))

(def buffer (ref []))

(defn key-typed [event]
  (let [c (.getKeyChar event)]
    (dosync
     (alter buffer conj c)))
  (println (apply str @buffer)))

(defn key-pressed [event]
  (println "key pressed " event))

(defn key-released [event])

(def keylistener
  (proxy [KeyListener] []
    (keyTyped [event]
      (key-typed event))
    (keyPressed [event]
      (key-pressed event))
    (keyReleased [event]
      (key-released event))))

(def panel (JPanel.))
(.addKeyListener panel keylistener)

(def frame (JFrame.))
(.setDefaultCloseOperation frame JFrame/DISPOSE_ON_CLOSE)
(.add frame panel)
(.pack frame)
(.requestFocus panel) ;; perhaps on an expose listener?

;;; (.setVisible frame true)
