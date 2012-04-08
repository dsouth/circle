(ns circle.repl
  (:require [circle.dispatch :as dispatch])
  (:import (java.io Reader Writer)))

(defn fire-type [s]
  (doseq [c s]
    (dispatch/fire :key-typed c)))

(def gui-writer
  (proxy [Writer] []
    (close [])
    (flush [])
    (write
      ([a]
         (if (instance? Long a)
           (dispatch/fire :key-typed (char a))
           (fire-type a)))
      ([a b c]
         (fire-type
          (str "write:" a ", " b ", " c " where class is " (class a)))))))

(def gui-reader
  (proxy [Reader] []
    (close [])
    (mark [])
    (markSupported [] false)
    (read
      ([])
      ([char-a])
      ([char-a off len]))
    (ready [] false)
    (reset [])
    (skip [x])))

;;; for testing
;;; (binding [*out* gui-writer *in* *in*]
;;;   (clojure.main/main))
