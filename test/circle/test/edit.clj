(ns circle.test.edit
  (:use [midje.sweet])
  (:use circle.edit))

(facts "about adding character"
       (add-char-to-line-at (vec "bcd") 0 \a) => (vec "abcd")
       (add-char-to-line-at (vec "abde") 2 \c) => (vec "abcde")
       (add-char-to-line-at (vec "abcd") 4 \e) => (vec "abcde"))

(facts "about deleting character"
       (delete-char-at (vec "zabc") 0) => (vec "abc")
       (delete-char-at (vec "abzcd") 2) => (vec "abcd")
       (delete-char-at (vec "abcdz") 4) => (vec "abcd"))
