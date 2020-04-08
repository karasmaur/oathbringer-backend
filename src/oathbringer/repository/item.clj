(ns oathbringer.repository.item
  (:require [oathbringer.util.db-util :refer [transact-single-entity query-db convert-datom-to-map]]
            [nano-id.core :refer [nano-id]]))

(defn create-item-tx [item]
  {:item/external-id (nano-id 10)
   :item/name (item :name)
   :item/weight (item :weight)})

(defn create-item [item]
  (transact-single-entity (create-item-tx item)))

(def find-all-items
  '[:find ?external-id ?name ?weight
    :where
    [?e :item/external-id ?external-id]
    [?e :item/name ?name]
    [?e :item/weight ?weight]])

(def item-schema
  [;;Items
   {:db/ident       :item/external-id
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one
    :db/doc         "An item's name"}
   {:db/ident       :item/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "An item's name"}
   {:db/ident       :item/weight
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one
    :db/doc         "An item's weight"}])