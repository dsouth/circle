(ns circle.state
  (:require [circle.dispatch :as dispatch]))

(def cursor-line (ref 0))
(def cursor-x (ref 0))
;; buffer is a vector of line vectors
(def buffer (ref [[]]))

(defn add-watcher [f]
  (add-watch buffer :buffer f)
  (add-watch cursor-x :cursor-x f)
  (add-watch cursor-line :cursor-line f))

;; state getters
(defn longest-line-count
  "Return the count of the longest line in the buffer"
  []
  (apply max (map count @buffer)))

(defn line-count []
  (count @buffer))

(defn get-line [i]
  (apply str (@buffer i)))

(defn get-cursor-line []
  @cursor-line)

(defn load-buffer [b]
  (dosync (ref-set buffer b)
          (ref-set cursor-line 0)
          (ref-set cursor-x 0)))

;; buffer modification via user interactions
(defn modify-buffer [f]
  "Used to add a newline at the cursor to the buffer."
  (dosync
   (alter buffer f @cursor-line @cursor-x)
   (alter cursor-line inc)
   (ref-set cursor-x 0)))

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
     (ref-set cursor-x new-x))))

(defn move-cursor [[line-num x]]
  "Moves the cursor to line number line-num and horizontal position x."
  (dosync
   (ref-set cursor-line line-num)
   (ref-set cursor-x x)))

(defn get-text-from
  "Gets a string that represents the text from x and line to the current
   cursor position in the buffer."
  [{x :x line :line}]
  (->> (if (= l line)
         (subvec b x)
         b)
       (let [b (@buffer l)])
       (for [l (range line (inc @cursor-line))])
       (interpose \newline)
       (flatten)
       (apply str)))
