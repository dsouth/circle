(ns ide.core)
(import
 '(javax.swing JFrame JPanel)
 '(java.awt.event KeyEvent  KeyListener))

(def buffer (ref []))

(defn delete []
  (println "deleteing ...")
  (let [end (- (count @buffer) 1)]
    (dosync
     (alter buffer subvec 0 end))))

(defn add-char [event]
  (let [c (.getKeyChar event)]
    (dosync
     (alter buffer conj c))))

(defn key-typed [event])

(defn key-pressed [event]
  (if (= KeyEvent/VK_BACK_SPACE (.getKeyCode event))
    (delete)
    (add-char event))
  (println (apply str @buffer)))

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
(.requestFocus panel) ;; perhaps on an expose listener? Or a focus manager???

(defn show []
  (.setVisible frame true))
