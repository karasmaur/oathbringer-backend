(ns oathbringer.repository.user
  (:require [oathbringer.repository.db-util :refer [transact-single-entity query-db convert-datom-to-map]]
            [nano-id.core :refer [nano-id]]))

(def user-schema [{:db/ident        :user/external-id
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/unique :db.unique/identity
                   :db/doc         "A user's external ID to be exposed in the API"}

                  {:db/ident        :user/name
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc         "A user's complete name"}

                  {:db/ident :user/email
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc "A user's e-mail"}])

(defn user-return [user]
  "Returns a map with the user's fields"
  {:external-id (nth user 0) :name (nth user 1) :email (nth user 2)})

(defn convert-to-datomic-map [user]
  "Returns a user map prepared to add in Datomic."
  {:user/external-id (nano-id 10)
   :user/name (str (user :name))
   :user/email (str (user :email))})

(defn create [user]
  "Returns the user after transacting it to datomic"
  (user-return (convert-datom-to-map (transact-single-entity (convert-to-datomic-map user)))))

(def all-users-query '[:find ?external-id ?name ?email
                       :where [?e :user/external-id ?external-id]
                       [?e :user/name ?name]
                       [?e :user/email ?email]])

(defn find-all []
  "Returns a list of all users in datomic"
  (map user-return (query-db all-users-query)))



