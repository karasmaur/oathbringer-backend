(ns oathbringer.repository.user
  (:require [oathbringer.repository.db-util :refer [transact query-db convert-datom]]))

(def user-schema [{:db/ident        :user/firstname
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc         "A user's firstname"}

                  {:db/ident :user/surname
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc "A user's surname"}])

(defn user-return [user]
  "Returns a map with the user's fields"
  {:firstname (nth user 0) :surname (nth user 1)})

(defn add-user [user]
  "Returns the user after transacting it to datomic"
  (user-return (convert-datom (transact user))))

(def all-users-query '[:find ?firstname ?surname
                       :where [?e :user/firstname ?firstname]
                       [?e :user/surname ?surname]])

(defn get-all-users []
  "Returns a list of all users in datomic"
  (map user-return (query-db all-users-query)))



(def test-user {:user/firstname "test" :user/surname "surtest"})
(def transact-test (add-user test-user))