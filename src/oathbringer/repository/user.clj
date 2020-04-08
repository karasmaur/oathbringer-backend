(ns oathbringer.repository.user
  (:require [oathbringer.util.db-util :refer [transact-single-entity query-db convert-datom-to-map]]
            [nano-id.core :refer [nano-id]]
            [buddy.hashers :as hashers]))

(def user-schema [{:db/ident        :user/external-id
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/unique :db.unique/identity
                   :db/doc         "A user's external ID to be exposed in the API"}
                  {:db/ident        :user/name
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/doc         "A user's complete name"}
                  {:db/ident        :user/email
                   :db/valueType    :db.type/string
                   :db/unique :db.unique/identity
                   :db/cardinality  :db.cardinality/one
                   :db/doc          "A user's e-mail"}
                  {:db/ident         :user/password
                   :db/valueType    :db.type/string
                   :db/cardinality  :db.cardinality/one
                   :db/doc          "A user's hashed password"}])

(defn user-return [user]
  "Returns a map with the user's fields"
  {:external-id (nth user 0) :name (nth user 1) :email (nth user 2)})

(defn convert-to-datomic-map [user]
  "Returns a user map prepared to add in Datomic."
  {:user/external-id (nano-id 10)
   :user/name (str (user :name))
   :user/email (str (user :email))
   :user/password (hashers/encrypt (str (user :password)))})

(defn create-user [user]
  "Returns the user after transacting it to datomic"
  (user-return (convert-datom-to-map (transact-single-entity (convert-to-datomic-map user)))))

(def all-users-query '[:find ?external-id ?name ?email
                       :where [?e :user/external-id ?external-id]
                              [?e :user/name ?name]
                              [?e :user/email ?email]])

(defn user-by-id-query [user-external-id] '[:find ?external-id ?name ?email ?password
                                            :where [?e :user/external-id ?external-id]
                                                   [?e :user/name ?name]
                                                   [?e :user/email ?email]
                                                   [?e :user/password ?password]
                                                   [?e :user/external-id user-external-id]])

(def user-by-email-query '[:find ?external-id ?name ?email
                           :in $ ?user-email
                           :where [?e :user/email ?user-email]
                                  [?e :user/external-id ?external-id]
                                  [?e :user/name ?name]
                                  [?e :user/email ?email]])

(def user-id-by-external-id '[:find ?e
                          :in $ ?external-id
                          :where [?e :user/external-id ?external-id]])

(def user-password-by-email '[:find ?password
                              :in $ ?user-email
                              :where [?e :user/email ?user-email]
                                     [?e :user/password ?password]])

(defn password-match? [email password]
  "Check to see if the password given matches the digest of the user's saved password"
  (hashers/check password (ffirst (query-db user-password-by-email email))))

(defn email-exists? [email]
  "Returns true if the user's email exists in the database."
  (if (= (query-db user-by-email-query email) '[])
    false
    true))


(defn find-all-users []
  "Returns a list of all users in datomic"
  (map user-return (query-db all-users-query)))

(defn find-user-id [external-id]
  (ffirst (query-db user-id-by-external-id external-id)))

