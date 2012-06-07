(ns circle.repl
  (:require [circle.dispatch :as dispatch]
            [circle.event :as event])
  (:import (java.awt.event KeyEvent)
           (java.io PushbackReader StringReader)))

(defn read-string
  "Given a String form, returns either a Clojrue data structure that
for the String form or nil if the form is not valid."
  [form]
  (read (PushbackReader. (StringReader. form))))

(event/add-to-meta-map KeyEvent/VK_E :eval)
