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

(defn fire [e d]
  (println e)
  (let [s (seq (e @reactors))]
    (if s
      (doseq [f s]
        (f d))
      (println "WARNING: no reactor for " e "!!"))))

(defn receive [e]
  (let [f (e @askors)]
    (when f
      (f))))
