(ns oathbringer.datomic-access
  (:require   [datomic.client.api :as d]))

(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"
          :validate-hostnames false})

(def client (d/client cfg))

(def conn (d/connect client {:db-name "hello"}))

(def db (d/db conn))

(def user-schema [{:db/ident        :user/firstname
                    :db/valueType   :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc         "A user's firstname"}

                   {:db/ident :user/surname
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "A user's surname"}])

(defn transact-all-schemas [] (d/transact conn {:tx-data user-schema}))

(defn convert-datom [transaction-return]
  "Returns a list with only the transacted data from the returned map of d/transact"
  (map #(:v %) (drop 1 (:tx-data transaction-return))))

(defn user-return [user]
  "Returns a map with the user's fields"
  {:firstname (nth user 0) :surname (nth user 1)})

(defn transact-user [user]
  "Returns the user after transacting it to datomic"
  (user-return (convert-datom (d/transact conn {:tx-data (list user)}))))

(def query-all-users '[:find ?firstname ?surname
                      :where [?e :user/firstname ?firstname]
                      [?e :user/surname ?surname]])

(defn get-all-users []
  "Returns a list of all users in datomic"
  (map user-return (d/q query-all-users db)))

(def test-user {:user/firstname "test" :user/surname "surtest"})
(def transact-test (transact-user test-user))