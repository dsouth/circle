(ns circle.test.edit
  (:use [midje.sweet])
  (:use circle.edit))

(facts "about adding character"
       (add-char-to-line-at (vec "bcd") 0 \a) => (vec "abcd")
       (add-char-to-line-at (vec "abde") 2 \c) => (vec "abcde")
       (add-char-to-line-at (vec "abcd") 4 \e) => (vec "abcde"))

(facts "about deleting character"
       (delete-char-at (vec "zabc") 1) => (vec "abc")
       (delete-char-at (vec "abzcd") 3) => (vec "abcd")
       (delete-char-at (vec "abcdz") 5) => (vec "abcd")
       (delete-char-at (vec "abc") 1) => (vec "bc")
       (delete-char-at (vec "abc") 2) => (vec "ac")
       (delete-char-at (vec "abc") 3) => (vec "ab"))

(comment "delete at 0 should be removing the newline before the
          beginning of a line")

(facts "about adding a newline"
       (add-newline (vec "abc") 0) => [[] (vec "abc")]
       (add-newline (vec "abc") 3) => [(vec "abc") []]
       (add-newline (vec "abc") 1) => [(vec "a") (vec "bc")]
       (add-newline [] 0) => [[] []])

(facts "about adding newline to document"
       (add-newline-at-cursor [(vec "first") (vec "second") (vec "third")] 1 3)
       => [(vec "first")
           (vec "sec")
           (vec "ond")
           (vec "third")]
       (add-newline-at-cursor [(vec "first") (vec "second") (vec "third")] 0 3)
       => [(vec "fir")
           (vec "st")
           (vec "second")
           (vec "third")]
       (add-newline-at-cursor [(vec "first") (vec "second") (vec "third")] 0 0)
       => [[]
           (vec "first")
           (vec "second")
           (vec "third")]
       (add-newline-at-cursor [(vec "first") (vec "second") (vec "third")] 2 0)
       => [(vec "first")
           (vec "second")
           []
           (vec "third")])
