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

(facts "about combining lines"
       (mush [[] []]) => [[]]
       (mush [[] (vec "a")]) => [(vec "a")]
       (mush [(vec "a") []]) => [(vec "a")]
       (mush [(vec "a") (vec "b")]) => [(vec "ab")])

(facts "about deleting a line"
        (delete-line [(vec "abc") (vec "def")] 1) => [(vec "abcdef")]
        (delete-line [(vec "abc") []] 1) => [(vec "abc")]
        (delete-line [(vec "abc")
                       (vec "def")
                       (vec "ghi")] 1) => [(vec "abcdef") (vec "ghi")])

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

(facts "about moving the cursor"
       (forward [(vec "first")] 0 0) => [0 1]
       (forward [(vec "first")] 0 1) => [0 2]
       (backward [(vec "first")] 0 5) => [0 4]
       (backward [(vec "first")] 0 4) => [0 3]
       (backward [(vec "first")] 0 1) => [0 0]
       (up [(vec "fisrt") (vec "second")] 1 0) => [0 0]
       (up [(vec "first") (vec "second")] 1 1) => [0 1]
       (up [(vec "first") (vec "second")] 1 6) => [0 5]
       (up [(vec "1")] 0 0) => [0 0]
       (up [(vec "1") (vec "2") (vec "3")] 2 1) => [1 1]
       (down [(vec "first") (vec "second")] 0 0) => [1 0]
       (down [(vec "frist") (vec "second")] 0 1) => [1 1]
       (down [(vec "long first") (vec "second")] 0 10) => [1 6]
       (down [(vec "1")] 0 0) => [0 0]
       (down [(vec "1") (vec "2") (vec "3")] 1 0) => [2 0])

(facts "about moving past the end of the line"
       (forward [(vec "first")
                 (vec "second")] 0 5) => [1 0])

(facts "about moving past the beginning of a line"
       (backward [(vec "first")
                  (vec "second")] 1 0) => [0 5])

(facts "about not moving past the end of the document"
       (forward [(vec "first")] 0 5) => [0 5])

(facts "about moving past the beginning of the document"
       (backward [(vec "first")] 0 0) => [0 0])
