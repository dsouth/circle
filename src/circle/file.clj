(ns circle.file
  (:require [circle.dispatch :as dispatch]))

;; source file loading
(defn load-source [file]
  (let [data (with-open [rdr (clojure.java.io/reader file)]
               (doall (line-seq rdr)))
        text (vec (map vec data))]
    text))

(defn load-buffer [file]
  (dispatch/fire :state-load-buffer (load-source file)))
