(ns circle.repl
  (:require [circle.dispatch :as dispatch])
  (:import (java.io Writer)))

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
