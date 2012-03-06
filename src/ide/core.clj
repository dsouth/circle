(ns ide.core)
(import
 '(javax.swing JFrame JPanel)
 '(java.awt.event KeyEvent  KeyListener))

(def buffer (ref []))

(defn key-typed [event]
  (let [c (.getKeyChar event)]
    (dosync (alter buffer conj c)))
  (println @buffer))

(defn key-pressed [event]
  (if (= KeyEvent/VK_BACK_SPACE (.getKeyCode event))
    (do
      (println "back space" (butlast @buffer))
      (dosync (alter buffer subvec 0 (- (count buffer) 1))))))

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

(defn show []
  (.setVisible frame true))
