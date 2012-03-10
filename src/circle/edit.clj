(ns circle.edit)

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
      (dec x)
      x)))

(defn dummy [_ line-length]
  line-length)

(defn delete-eol []
  (let [new-size (dec (count @buffer))
        line-length (count (get-line (dec @cursor-line)))]
    (dosync
     (alter buffer subvec 0 new-size)
     (alter cursor-line #(dec %))
     (alter cursor-x dummy line-length))))

(defn delete-char []
  (let [line-number @cursor-line
        end (dec (count (@buffer @cursor-line)))
        altered (subvec (@buffer line-number) 0 end)]
    (dosync
     (alter buffer assoc line-number altered)
     (alter cursor-x #(dec %)))))

(defn delete []
  (let [line-number @cursor-line]
    (if (= [] (@buffer line-number))
      (if (> (count @buffer) 1)
        (delete-eol))
      (delete-char))))

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
  (cond
   (= c \newline) (add-newline-end-of-line)
   (Character/isDefined c) (add-char-end-of-line @cursor-line
                                                 (conj (@buffer @cursor-line) c))))
