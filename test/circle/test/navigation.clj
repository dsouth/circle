(ns circle.test.navigation
  (:use midje.sweet
        circle.navigation))

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
