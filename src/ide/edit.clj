(ns ide.edit)

(def cursor-line (ref 0))
(def cursor-x (ref 0))
;; buffer is a vector of line vectors
(def buffer (ref [[]]))

(defn line-count []
  (count @buffer))

(defn get-line [i]
  (apply str (@buffer i)))

(defn get-cursor-line []
  @cursor-line)

(defn get-horizontal-cursor-position []
  (let [x @cursor-x]
    (if (> x 0)
      (- x 1)
      x)))

(defn dummy [_ line-length]
  line-length)

(defn delete []
  (let [line-number @cursor-line]
    (if (= [] (@buffer line-number))
      (if (> (count @buffer) 1)
        (let [new-size (- (count @buffer) 1)
              line-length (count (get-line (- @cursor-line 1)))]
          (dosync
           (alter buffer subvec 0 new-size)
           (alter cursor-line #(- % 1))
           (alter cursor-x dummy line-length))))
      (let [end (- (count (@buffer @cursor-line)) 1)
            altered (subvec (@buffer line-number) 0 end)]
        (dosync
         (alter buffer assoc line-number altered)
         (alter cursor-x #(- % 1)))))))

(defn can-add-char? [c]
  (and (Character/isDefined c)
       (not= c \newline)))

(defn add-char-end-of-line [line-number new-line-text]
  (dosync
   (alter buffer assoc line-number new-line-text)
   (alter cursor-x #(inc %))))

(defn add-newline-end-of-line []
  (dosync
   (alter buffer assoc (inc @cursor-line) [])
   (alter cursor-line #(inc %))
   (alter cursor-x #(* % 0))))

(defn add-char [c]
  (if (can-add-char? c)
    (add-char-end-of-line @cursor-line (conj (@buffer @cursor-line) c))
    (if (= c \newline)
      (add-newline-end-of-line))))
