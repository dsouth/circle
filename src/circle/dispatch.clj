(ns circle.dispatch)

(def reactors (ref {}))
(def askors (ref {}))

(defn add-reactor [e f]
  (if-let [s (e @reactors)]
    (let [new-s (conj s f)]
      (dosync
       (alter reactors assoc e new-s)))
    (dosync
     (alter reactors assoc e #{f}))))

(defn add-askor [e f]
  (dosync
   (alter askors assoc e f)))

(defn remover [reactor]
  (fn [m]
    (let [k (key m)
          s (val m)]
      [k (disj s reactor)])))

(defn empty-set-in-map? [m]
  (empty? (m 1)))

(defn remove-reactor [r]
    (dosync
     (alter reactors #(into {} (remove empty-set-in-map? (map (remover r) %))))))

(defn fire
  "Given an event e and data d, will fire off the function(s) that are mapped to e, via add-reactor, with parameter d. Returns nil."
  [e d]
  (let [s (seq (e @reactors))]
    (if s
      (doseq [f s]
        (f d))
      (println "WARNING: no reactor for" e "!!"))))

(defn receive
  "Given an event e, will return the value of the function that e is mapped via add-askor."
  [e]
  (let [f (e @askors)]
    (if f
      (f)
      (println "WARNING: no reciever for" e "!!"))))
