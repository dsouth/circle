(ns circle.repl
  (:require [circle.dispatch :as dispatch]
            [circle.event :as event]
            [clojure.tools.nrepl :as repl]
            [clojure.tools.nrepl.server :as server])
  (:import (java.awt.event KeyEvent)
           (java.io PushbackReader StringReader)))

(defonce server (server/start-server :port 7888))

(defn eval [s]
  (with-open [conn (repl/connect :port 7888)]
    (-> (repl/client conn 1000)
        (repl/message {:op "eval" :code s})
        repl/response-values)))
