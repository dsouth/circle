(ns circle.repl
  (:require [circle.dispatch :as dispatch]
            [circle.event :as event]
            [clojure.tools.nrepl :as repl]
            [clojure.tools.nrepl.server :as server])
  (:import (java.awt.event KeyEvent)
           (java.io PushbackReader StringReader)))

(defn display-string [s]
  (doseq [c s]
    (dispatch/fire :key-typed c)))

(defonce server (server/start-server :port 7888))
(def from-x (ref 0))
(def from-line (ref 0))
;;; TODO ns doesn't really "stick..." :(
(def repl-ns (ref "user"))
(def repl-value (ref ()))

(defn result [s]
  (println "-- message start from nREPL --")
  (doseq [m s]
    (println m)
    (let [n (:ns m)
          v (:value m)]
      (when n
        (dosync (ref-set repl-ns n)))
      (when v
        (dosync (ref-set repl-value v)))))
  (println "-- message end from nREPL --"))

(defn eval-string [s]
  (with-open [conn (repl/connect :port 7888)]
    (-> (repl/client conn 1000)
        (repl/message {:op "eval" :code s :ns @repl-ns})
        doall
        result)))

(defn prompt []
  (display-string (str @repl-ns "=> "))
  (dosync
   (ref-set from-x (dispatch/receive :state-get-cursor-x))
   (ref-set from-line (dispatch/receive :state-get-cursor-line))))

(defn enter? [{modifier :modifier key :key}]
  (and (= modifier 0)
       (= key \newline)))

(defn key-event [event]
  (when (enter? event)
    (let [input (dispatch/receive :state-get-text-from {:x @from-x :line @from-line})
          e-string (eval-string input)]
      (display-string (str @repl-value \newline)))
    (prompt)))
