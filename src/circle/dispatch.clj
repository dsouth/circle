(ns circle.dispatch)

(def reactors "Map of events to functions that react to the event" (ref {}))
(def producers "Map of events to functions that return a value for the event"(ref {}))

(defn add-reactor
  "Given an event e, adds the function f as a reactor to the event. Can add multiple
function for one event, i.e. events to functions is 1 to many."
  [e f]
  (if-let [s (e @reactors)]
    (let [new-s (conj s f)]
      (dosync
       (alter reactors assoc e new-s)))
    (dosync
     (alter reactors assoc e #{f}))))

(defn add-producer
  "Given an event e, adds the function f as a function that will return
a value for the event. Only one function per event, i.e. 1 to 1."
  [e f]
  (dosync
   (alter producers assoc e f)))

(defn remover [reactor]
  (fn [m]
    (let [k (key m)
          s (val m)]
      [k (disj s reactor)])))

(defn empty-set-in-map? [m]
  (empty? (m 1)))

(defn remove-reactor [r]
  "Removes the reactor function r from the reactors, regardless of what event it reacts to"
    (dosync
     (alter reactors #(into {} (remove empty-set-in-map? (map (remover r) %))))))

(defn fire
  "Given an event e and data d, will fire off the function(s) that are mapped to e, via add-reactor, with parameter d. Returns nil."
  [e d]
  (println "fire -> " e d)
  (let [s (seq (e @reactors))]
    (if s
      (doseq [f s]
        (f d))
      (println "WARNING: no reactor for" e "!!"))))

(defn receive
  "Given an event e, will return the value of the function that e is mapped via add-askor."
  ([e]
     (receive e nil))
  ([e d]
     (let [f (e @producers)]
       (if f
         (if d
           (f d)
           (f))
         (println "WARNING: no receiver for" e "!!")))))
