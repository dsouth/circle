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

(defn delete-char-at [v i]
  (if (= i (count v))
    (subvec v 0 (dec i))
    (apply conj (subvec v 0 (dec i)) (subvec v i))))

(defn dummy [_ line-length]
  line-length)

(defn delete-eol []
  (let [new-size (dec (count @buffer))
        line-length (count (get-line (dec @cursor-line)))]
    (dosync
     (alter buffer subvec 0 new-size)
     (alter cursor-line #(dec %))
     (alter cursor-x dummy line-length))))

(defn delete-char-before-cursor []
  (let [line-number @cursor-line
        end (dec (count (@buffer @cursor-line)))
        altered (delete-char-at (@buffer @cursor-line) @cursor-x)]
    (dosync
     (alter buffer assoc line-number altered)
     (alter cursor-x #(dec %)))))

(defn delete []
  (let [line-number @cursor-line]
    (if (= 0 @cursor-x)
      (if (> (count @buffer) 1)
        (delete-eol))
      (delete-char-before-cursor))))

(defn modify-buffer-line [line-number new-line-text]
  (dosync
   (alter buffer assoc line-number new-line-text)
   (alter cursor-x #(inc %))))

(defn add-newline [v x]
  (if (= x (count v))
    [v []]
    [(subvec v 0 x) (subvec v x)]))

(defn adding-newline-at-end-of-document? [buffer line]
  (= (inc line) (count buffer)))

(defn new-document-with-modification [start-of-document buffer line]
  (apply conj
         start-of-document
         (subvec buffer (inc line))))

(defn add-newline-at-cursor [buffer line x]
  (let [start-of-document (apply conj
                                 (subvec buffer 0 line)
                                 (add-newline (buffer line) x))]
    (if (adding-newline-at-end-of-document? buffer line)
      start-of-document
      (new-document-with-modification start-of-document buffer line))))

(defn add-newline-end-of-line []
  (dosync
   (alter buffer assoc (inc @cursor-line) [])
   (alter cursor-line #(inc %))
   (alter cursor-x #(* % 0))))

(defn add-to-eol? [v i]
  (= i (count v)))

(defn add-char-at-eol [v x]
  (conj v x))

(defn add-char-at [v i x]
    (apply conj (conj (subvec v 0 i) x) (subvec v i)))

(defn add-char-to-line-at [v i x]
  (if (add-to-eol? v i)
    (add-char-at-eol v x)
    (add-char-at v i x)))

(defn newline? [c]
  (= c \newline))

(defn character? [c]
  (Character/isDefined c))

(defn add-char [c]
  (cond
   (newline? c) (add-newline-end-of-line)
   (character? c) (modify-buffer-line @cursor-line
                                      (add-char-to-line-at (@buffer @cursor-line) @cursor-x c))))
