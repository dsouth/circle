(ns circle.repl
  (:require [circle.dispatch :as dispatch]
            [circle.event :as event]
            [circle.core :as core])
  (:import (java.awt.event KeyEvent)
           (java.io PushbackReader StringReader)))

(def from-x (ref 0))
(def from-line (ref 0))

(defn string->data
  "Given a String form, returns either a Clojrue data structure that
for the String form or nil if the form is not valid."
  [form]
  (try
    (read (PushbackReader. (StringReader. form)))
    (catch RuntimeException _ nil)))

(defn display-string [s]
  (doseq [c s]
    (dispatch/fire :key-event {:modifier 0 :code 0 :key c})))

(defn prompt []
  (display-string (str *ns*))
  (display-string "=> ")
  (dosync
   (ref-set from-x (dispatch/receive :state-get-cursor-x))
   (ref-set from-line (dispatch/receive :state-get-cursor-line))))

(defn enter? [{modifier :modifier code :code}]
  (and (= modifier 0)
       (= code KeyEvent/VK_ENTER)))

(defn do-the-repl []
  (binding [*ns* *ns*]
    (let [s (dispatch/receive :state-get-text-from {:x @from-x :line @from-line})
          data (string->data s)
          result (eval data)]
      (display-string (str (if result result "nil") \newline))
      (prompt))))

(defn key-event [event]
  (when (enter? event)
    (do-the-repl)))

(dispatch/add-reactor :key-event key-event)

(core/main)
(prompt)
