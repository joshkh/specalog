(ns specalog.query
  (:require [specalog.jig :as jig]
            [clojure.spec.alpha :as s])
  (:import [java.util.UUID]))

(defn ruuid [] (java.util.UUID/randomUUID))

; Turn on assertion checking in order to throw Exceptions
(s/check-asserts true)

(defn pull-thing
  "Returns a datalog query where by returned entities meet the minimum requirements
   of a spec while pulling all potential values from a spec."
  ([spec] (pull-thing spec {}))
  ([spec constraints] (pull-thing spec constraints {}))
  ([spec constraints query]
   (->> query
        (jig/make-pull spec)
        (jig/make-in)
        (jig/make-where spec constraints))))


(defn put-thing
  "Returns a datalog query"
  ([data]
   (put-thing nil data))
  ([spec data]
   (cond-> {}
           ; If we conform to the spec then return a transaction query
           (s/assert spec data) (update :tx-data jig/conjv data) )))

