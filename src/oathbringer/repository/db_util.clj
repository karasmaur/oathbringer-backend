(ns oathbringer.repository.db-util
  (:require   [datomic.client.api :as d]))

(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"
          :validate-hostnames false})

(def client (d/client cfg))

(def conn (d/connect client {:db-name "hello"}))

(defn get-db
  "Returns de database from the connection"
  []
  (d/db conn))

(defn transact [data]
  (d/transact conn {:tx-data data}))

(defn transact-single-entity [data]
  (d/transact conn {:tx-data (list data)}))

(defn query-db
  ([query] (d/q query (get-db)))
  ([query parameter] (d/q query (get-db) parameter)))

(defn convert-datom-to-map [transaction-return]
  "Returns a list with only the transacted data from the returned map of d/transact"
  (map #(:v %) (drop 1 (:tx-data transaction-return))))
