(ns circle.state
  (:require [circle.utils :as utils]))

(def cursor-line (ref 0))
(def cursor-x (ref 0))
;; buffer is a vector of line vectors
(def buffer (ref [[]]))

;; source file loading
(defn load-source [file]
  (let [data (with-open [rdr (clojure.java.io/reader file)]
               (doall (line-seq rdr)))
        text (vec (map vec data))]
    text))

(defn load-buffer [file]
  (dosync
   (alter buffer utils/dummy (load-source file))))

;; buffer modification via user interactions
(defn modify-buffer [f]
  "Used to add a newline at the cursor to the buffer."
  (dosync
   (alter buffer f @cursor-line @cursor-x)
   (alter cursor-line inc)
   (alter cursor-x utils/dummy 0)))

(defn modify-buffer-line [new-line-text]
  (let [line-number @cursor-line]
    (dosync
     (alter buffer assoc line-number new-line-text)
     (alter cursor-x #(inc %)))))

(defn delete-char-before-cursor [altered]
  (let [line-number @cursor-line
        end (dec (count (@buffer @cursor-line)))]
    (dosync
     (alter buffer assoc line-number altered)
     (alter cursor-x #(dec %)))))

(defn delete-line [f]
  (let [new-x (count (@buffer (dec @cursor-line)))]
    (dosync
     (alter buffer f @cursor-line)
     (alter cursor-line dec)
     (alter cursor-x utils/dummy new-x))))
