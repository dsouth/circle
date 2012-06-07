(ns circle.repl
  (:require [circle.dispatch :as dispatch])
  (:import (java.io PrintWriter PushbackReader Reader StringReader Writer)
           (javax.swing SwingUtilities)))

(defn read-string
  "Given a String form, returns either a Clojrue data structure that
for the String form or nil if the form is not valid."
  [form]
  (read (PushbackReader. (StringReader. form))))
