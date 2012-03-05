(ns ide.core)
(import
 '(javax.swing JFrame JPanel)
 '(java.awt.event KeyEvent  KeyListener))

(def keylistener
  (proxy [KeyListener] []
    (keyTyped [event]
      (println "keyTyped " (.getKeyChar event)))
    (keyPressed [event]
      println "keyPressed " (.getKeyChar event))
    (keyReleased [event]
      println "keyReleased " (.getKeyChar event))))

(def panel (JPanel.))
(.addKeyListener panel keylistener)

(def frame (JFrame.))
(.setDefaultCloseOperation frame JFrame/DISPOSE_ON_CLOSE)
(.add frame panel)
(.pack frame)
(.requestFocus panel) ;; perhaps on an expose listener?

;;; (.setVisible frame true)
