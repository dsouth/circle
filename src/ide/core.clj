(ns ide.core)
(import
 '(javax.swing JFrame JPanel)
 '(java.awt.event KeyListener))

(def keylistener
  (proxy [KeyListener] []
    (keyTyped [event]
      (println "keyTyped " event))
    (keyPressed [event]
      println "keyPressed " event)
    (keyReleased [event]
      println "keyReleased " event)))

(def panel (JPanel.))

(.addKeyListener panel keylistener)

(def frame (JFrame.))
(.setDefaultCloseOperation frame JFrame/DISPOSE_ON_CLOSE)
(.add frame panel)
(.pack frame)
;;; (.setVisible frame true)
