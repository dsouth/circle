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

(defn eval-string [s]
  (with-open [conn (repl/connect :port 7888)]
    (-> (repl/client conn 1000)
        (repl/message {:op "eval" :code s})
        repl/response-values)))

(defn prompt []
  (display-string "user=> ")
  (dosync
   (ref-set from-x (dispatch/receive :state-get-cursor-x))
   (ref-set from-line (dispatch/receive :state-get-cursor-line))))

(defn enter? [{modifier :modifier key :key}]
  (and (= modifier 0)
       (= key \newline)))

(defn key-event [event]
  (println event)
  (when (enter? event)
    (let [input (dispatch/receive :state-get-text-from {:x @from-x :line @from-line})
          e-string (eval-string input)]
      (println "input" input)
      (println "evaluated to" e-string))
    (prompt)))
