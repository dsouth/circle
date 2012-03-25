(ns circle.state
  (:require [circle.utils :as utils]))

(def cursor-line (ref 0))
(def cursor-x (ref 0))
;; buffer is a vector of line vectors
(def buffer (ref [[]]))

(defn load-source [file]
  (let [data (with-open [rdr (clojure.java.io/reader file)]
               (doall (line-seq rdr)))
        text (vec (map vec data))]
    text))

(defn load-buffer [file]
  (dosync
   (alter buffer utils/dummy (load-source file))))
