(ns circle.test.dispatch
  (:use [midje.sweet]
        [circle.dispatch]))

(def stub-reactor "stub-reactor")

(against-background [(before :contents (dosync (ref-set reactors {})))
                     (after :contents (fact "add to no reactors"
                                            @reactors => {:test #{stub-reactor}}))]
                    (add-reactor :test stub-reactor))

(against-background [(before :contents (dosync (ref-set reactors {:test #{:f}})))
                     (after :contents (fact "add to some reactors"
                                            @reactors => {:test #{stub-reactor :f}}))]
                    (add-reactor :test stub-reactor))

(against-background [(before :contents (dosync (ref-set reactors {:test #{stub-reactor}})))
                      (after :contents (fact "remove reactor" @reactors => {}))]
                    (remove-reactor stub-reactor))

(against-background [(before :contents (dosync (ref-set reactors {:test #{:other stub-reactor}})))
                     (after :contents (fact "remove reactor leaves others" @reactors => {:test #{:other}}))]
                    (remove-reactor stub-reactor))

(def results (ref []))
(defn record-results [d]
  (dosync
   (alter results conj d)))

(against-background [(before :contents (dosync (ref-set results [])))
                     (after :contents (fact "fired to listener" @results => [:data]))]
                    (add-reactor :test record-results)
                    (fire :test :data))

(against-background [(before :contents (dosync (ref-set results [])))
                     (after :contents (fact "fired ignores those not registered" @results => []))]
                    (add-reactor :test record-results)
                    (remove-reactor record-results)
                    (fire :test :data))

(defn answer [] 42)

(against-background [(before :contents (dosync (ref-set producers {})))
                     (after :contents (fact "add to no producers"
                                            @producers => {:test :function}))]
                    (add-producer :test :function))

(against-background [(before :contents (dosync (ref-set producers {:get :blah})))
                     (after :contents (fact "add to other producers"
                                            @producers => {:get :blah :test :function}))]
                    (add-producer :test :function))

(against-background [(before :facts (dosync (ref-set producers {:test answer})))]
                    (fact "askor returns value to askee"
                          (receive :test) => 42))

(against-background [(before :facts (dosync (ref-set producers {:test inc})))]
                    (fact "askor returns value with parameter to askee"
                          (receive :test 3) => 4))
