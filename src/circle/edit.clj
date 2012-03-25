(ns circle.edit
  (:require [circle.state :as state]))

(defn longest-line-count
  "Return the count of the longest line in the buffer"
  []
  (apply max (map count @state/buffer)))

(defn line-count []
  (count @state/buffer))

(defn get-line [i]
  (apply str (@state/buffer i)))

(defn get-cursor-line []
  @state/cursor-line)

(defn get-horizontal-cursor-position []
  @state/cursor-x)

(defn delete-char-at [v i]
  (if (= i (count v))
    (subvec v 0 (dec i))
    (apply conj (subvec v 0 (dec i)) (subvec v i))))

(defn dummy [_ x]
  x)

(defn load-source [file]
  (let [data (with-open [rdr (clojure.java.io/reader file)]
               (doall (line-seq rdr)))
        text (vec (map vec data))]
    text))

(defn load-buffer [file]
  (dosync
   (alter state/buffer dummy (load-source file))))

(defn mush [[a b]]
  (if (seq b)
    [(apply conj a b)]
    [a]))

(defn delete-line [v i]
  (let [head (subvec v 0 (dec i))
        altered (mush (subvec v (dec i) (inc i)))
        altered-head (apply conj head altered)]
    (if (< (inc i) (count v))
      (let [tail (subvec v (inc i))]
        (apply conj altered-head tail))
      altered-head)))

(defn delete-line-stateful []
  (let [new-x (count (@state/buffer (dec @state/cursor-line)))]
    (dosync
     (alter state/buffer delete-line @state/cursor-line)
     (alter state/cursor-line dec)
     (alter state/cursor-x dummy new-x))))

(defn delete-eol []
  (let [new-size (dec (count @state/buffer))
        line-length (count (get-line (dec @state/cursor-line)))]
    (dosync
     (alter state/buffer subvec 0 new-size)
     (alter state/cursor-line #(dec %))
     (alter state/cursor-x dummy line-length))))

(defn delete-char-before-cursor []
  (let [line-number @state/cursor-line
        end (dec (count (@state/buffer @state/cursor-line)))
        altered (delete-char-at (@state/buffer @state/cursor-line) @state/cursor-x)]
    (dosync
     (alter state/buffer assoc line-number altered)
     (alter state/cursor-x #(dec %)))))

(defn delete []
  (let [line-number @state/cursor-line]
    (if (= 0 @state/cursor-x)
      (if (> (count @state/buffer) 1)
        (delete-line-stateful))
      (delete-char-before-cursor))))

(defn modify-buffer-line [line-number new-line-text]
  (dosync
   (alter state/buffer assoc line-number new-line-text)
   (alter state/cursor-x #(inc %))))

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

(defn modify-buffer []
  (dosync
   (alter state/buffer add-newline-at-cursor @state/cursor-line @state/cursor-x)
   (alter state/cursor-line inc)
   (alter state/cursor-x dummy 0)))

(defn add-newline-end-of-line []
  (dosync
   (alter state/buffer assoc (inc @state/cursor-line) [])
   (alter state/cursor-line #(inc %))
   (alter state/cursor-x #(* % 0))))

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
   (newline? c) (modify-buffer)
   (character? c) (modify-buffer-line @state/cursor-line
                                      (add-char-to-line-at (@state/buffer @state/cursor-line) @state/cursor-x c))))

(defn forward [buffer line x]
  (cond
   (and (= (inc line) (count buffer))
        (= x (count (buffer line))))
   [line x]

   (< x (count (buffer line)))
   [line (inc x)]

   :otherwise
   [(inc line) 0]))

(defn backward [buffer line x]
  (cond
   (and (= 0 x line))
   [0 0]

   (> x 0)
   [line (dec x)]

   :otherwise
   (let [l (dec line)]
     [l (count (buffer l))])))

(defn vertical-movement [buffer line x p f]
  (if (p line buffer)
    (let [new-line (f line)
          length (count (buffer new-line))]
     (if (> x length)
       [new-line length]
       [new-line x]))
    [line x]))

(defn up-ok-to-move? [line _]
  (> line 0))

(defn up [buffer line x]
  (vertical-movement buffer line x up-ok-to-move? #(dec %)))

(defn down [buffer line x]
  (vertical-movement buffer line x #(< (inc %1) (count %2)) #(inc %)))

(defn- cursor-move [f]
  (let [result (f @state/buffer @state/cursor-line @state/cursor-x)]
    (dosync
     (alter state/cursor-line dummy (result 0))
     (alter state/cursor-x dummy (result 1)))))

(defn cursor-forward []
  (cursor-move forward))

(defn cursor-backward []
  (cursor-move backward))

(defn cursor-up []
  (cursor-move up))

(defn cursor-down []
  (cursor-move down))
