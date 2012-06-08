(ns circle.test.state
  (:use midje.sweet)
  (:use circle.state))

(against-background [(before :contents (dosync (ref-set buffer [(vec "line 1") (vec "line 2") (vec "line 3") (vec "")])
                                               (ref-set cursor-line 3)
                                               (ref-set cursor-x 0)))]
                    (fact
                     (get-text-from {:x 0 :line 2}) => "line 3\n"
                     (get-text-from {:x 1 :line 2}) => "ine 3\n"))
