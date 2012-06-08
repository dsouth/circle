(ns circle.repl
  (:require [circle.dispatch :as dispatch]
            [circle.event :as event])
  (:import (java.awt.event KeyEvent)
           (java.io PushbackReader StringReader)))

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

(defn- output-ns []
  )
