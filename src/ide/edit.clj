(ns ide.edit)

;; cursor is line number and character on line
;; e.g. [0 1] is after the first character on the first line
(def cursor (ref [0 0]))
;; buffer is a vector of line vectors
(def buffer (ref [[]]))

(declare get-line)

(defn delete []
  (let [line-number (@cursor 0)]
    (if (= [] (@buffer line-number))
      (if (> (count @buffer) 1)
        (let [new-size (- (count @buffer) 1)
              line-length (count (get-line (- (@cursor 0) 1)))]
          (dosync
           (alter buffer subvec 0 new-size)
           (alter cursor assoc 0 (- (@cursor 0) 1))
           (alter cursor assoc 1 line-length))))
      (let [end (- (count (@buffer (@cursor 0))) 1)
            altered (subvec (@buffer line-number) 0 end)]
        (dosync
         (alter buffer assoc line-number altered)
         (alter cursor assoc 1 (- (@cursor 1) 1)))))))

(defn can-add-char? [c]
  (and (Character/isDefined c)
       (not= c \newline)))

(defn add-char [c]
  (if (can-add-char? c)
    (let [line-number (@cursor 0)
          altered (conj (@buffer line-number) c)]
      (dosync
       (alter buffer assoc line-number altered)
       (alter cursor assoc 1 (+ (@cursor 1) 1))))
    (if (= c \newline)
      (dosync
       (alter buffer assoc (+ 1 (@cursor 0)) [])
       (alter cursor assoc 0 (+ (@cursor 0) 1))
       (alter cursor assoc 1 0)))))

(defn line-count []
  (count @buffer))

(defn get-line [i]
  (apply str (@buffer i)))

(defn cursor-line []
  (@cursor 0))

(defn end-of-line [x line]
  (let [end (= (count (@buffer line)) x)]
    (println "end of line is " end)
    end))

(defn get-horizontal-cursor-position []
  (let [x (@cursor 1)
        line (@cursor 0)]
    (if (> x 0)
      (- x 1)
      x)))

;; for debugging
(defn reset []
  (dosync (alter cursor assoc 0 0)
          (alter cursor assoc 1 0)
          (alter buffer vector [])))
