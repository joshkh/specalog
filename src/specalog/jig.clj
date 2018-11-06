(ns specalog.jig
  (:require [clojure.spec.alpha :as s]))

(def concatv (comp vec concat))
(def conjv (comp vec conj))

(def kw->symbol (fn [k] (symbol (str "?" (clojure.string/join "-" ((juxt namespace name) k))))))

(defn form->spec
  "Take the form of a map spec and turn it back into a map spec.
   In other words, identity function for a map spec"
  [spec]
  (->> spec
       ; Get the form of the spec
       s/form
       ; Take the good stuff
       rest
       ; Group the results into k/v pairs (:req, :opt)
       (partition 2)
       ; Pop it back into a map
       (reduce (fn [total [k v]] (assoc total k v)) {})))

(defn make-in
  "Ensure that a $ value is at the head of an :in clause"
  ([] (make-in {}))
  ([q]
   (cond-> q
           (not= '$ (get-in q [:in 0])) (update :in (comp vec (partial cons '$))))))

(defn make-pull
  "Add all :req and :opt values from a spec'ed map to the :find clause of a query"
  ([spec]
   (make-pull spec {}))
  ([spec q]
   (update q :find conjv (list 'pull (kw->symbol spec) (->> spec form->spec vals (apply concat) vec)))))

(defn filter-ands
  [constraints]
  (filter (fn [[k v]] (not (coll? v))) constraints))

(defn filter-ors
  [constraints]
  (filter (fn [[k v]] (coll? v)) constraints))

(defn make-where-a
  [constraints]
  (let [ands (filter-ands constraints)
        ors  (filter-ors constraints)]
    (println "ands" ands)

    ))

(defn make-where
  "Add all :req values from a spec'ed map to the :find vector of a query.
  This constrains the query to return only things that fully match a spec, aka match-all"
  ([spec]
   (make-where spec {}))
  ([spec constraints]
   (make-where spec constraints {}))
  ([spec constraints q]
   (cond-> q
           ; Apply user provided constraints first. Query optimisation? ¯\_(ツ)_/¯
           constraints (update :where concatv (mapcat (fn [[k v]]
                                                        [(vector (kw->symbol spec) k v)]) constraints))
           ; Append the :req values as datalog constraints
           ; ex. [?some-spec :some-spec/key]
           q (update :where concatv (map (fn [w] (vector (kw->symbol spec) w)) (->> spec form->spec :req vec))))))