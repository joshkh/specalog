(ns specalog.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(def full-string? (s/and string? (complement empty?)))

; Spec the entity's attributes
(s/def :person/uuid uuid?)
(s/def :person/first-name full-string?)
(s/def :person/last-name full-string?)
(s/def :person/email full-string?)
(s/def :person/password full-string?)

; ... and the entity itself as a map
(s/def :acme/person (s/keys :req [:person/uuid
                                  :person/email
                                  :person/password]
                            :opt [:person/first-name
                                  :person/last-name]))


; TODO - spec a generic Datalog query
;(pull ?root [:something])
;(s/def :datalog/pull-clause (s/and list? (s/tuple symbol? symbol? vector?)))
;(s/def :datalog/pull (s/coll-of :datalog/pull-clause))