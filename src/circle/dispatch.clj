(ns circle.dispatch)

(def reactors (ref {}))

(defn add-reactor [e f]
  (let [s (e @reactors)]
    (if s
      (let [new-s (conj s f)]
        (dosync
         (alter reactors assoc e new-s)))
      (dosync
       (alter reactors assoc e #{f})))))

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

(defn fire [e d]
  (println e)
  (let [s (seq (e @reactors))]
    (if s
      (doseq [f s]
        (f d))
      (println "WARNING: no reactor for " e "!!"))))
