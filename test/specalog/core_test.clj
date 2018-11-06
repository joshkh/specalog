(ns specalog.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [specalog.specs]
            [specalog.jig :as jig]
            [specalog.query :as q]))

;; spec helper functions

(deftest test-jig-hydrate-spec
  (testing "Convert a spec'ed map back into its spec definition"
    (is (=
          (jig/form->spec :acme/person)
          {:req [:person/uuid
                 :person/email
                 :person/password]
           :opt [:person/first-name
                 :person/last-name]}))))

;; make-in tests

(deftest test-jig-make-in-blank
  (testing "Prepend $ to an non-existing :in clause"
    (is (= (get (jig/make-in) :in)
           ['$]))))

(deftest test-jig-make-in-add-missing-db-$
  (testing "Prepend $ to an existing :in clause if it's not there"
    (is (= (get (jig/make-in {:in ['exists]}) :in)
           ['$ 'exists]))))

(deftest test-jig-make-in-$-already-exists
  (testing "Don't prepend $ to an existing :in clause if it's already there"
    (is (= (get (jig/make-in '{:in [$]}) :in)
           ['$]))))

;; pull a thing

(deftest test-pull-thing
  (testing "Pull a thing that matches a spec, returning all :req and :opt keys"
    (is (= (q/pull-thing :acme/person)
           '{:find  [(pull ?acme-person [:person/uuid :person/email
                                         :person/password
                                         :person/first-name
                                         :person/last-name])],
             :in    [$],
             :where [[?acme-person :person/uuid]
                     [?acme-person :person/email]
                     [?acme-person :person/password]]}))))

