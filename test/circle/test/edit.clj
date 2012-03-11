(ns circle.test.edit
  (:use [midje.sweet])
  (:use circle.edit))

(facts "about adding character"
       (add-char-to-line-at [2 3 4] 0 1) => [1 2 3 4]
       (add-char-to-line-at [1 2 4 5] 2 3) => [1 2 3 4 5]
       (add-char-to-line-at [1 2 3 4] 4 5) => [1 2 3 4 5])
