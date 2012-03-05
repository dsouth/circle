(ns ide.core)
(import
 '(javax.swing JFrame JPanel)
 '(java.awt.event KeyEvent  KeyListener))

(def buffer (ref []))

(defn add-char [c]
  (dosync
   (alter buffer conj c))
  (println (apply str @buffer)))

(def keylistener
  (proxy [KeyListener] []
    (keyTyped [event]
      (add-char (.getKeyChar event)))
    (keyPressed [event])
    (keyReleased [event])))

(def panel (JPanel.))
(.addKeyListener panel keylistener)

(def frame (JFrame.))
(.setDefaultCloseOperation frame JFrame/DISPOSE_ON_CLOSE)
(.add frame panel)
(.pack frame)
(.requestFocus panel) ;; perhaps on an expose listener?

;;; (.setVisible frame true)
