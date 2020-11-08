(ns oathbringer.util.db-util
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [monger.conversion :refer :all]
            [clojure.walk :refer :all]))

(def connect (mg/connect {:host "localhost" :port 27017}))

(def database
  (mg/get-db connect "oathbringer"))

(defn save-to-db [collection data] (mc/save-and-return database collection data))

(defn find-in-db [collection query] (mc/find-one database collection query))

(defn find-oid-in-db [collection query] (get (mc/find-one database collection query) "_id"))

(defn delete-from-db [colllection query]
  (mc/remove-by-id database colllection query))

(defn update-to-db [collection oid data]
  (mc/update database
                   collection
                   {:_id oid}
                   {$set data}))

(defn remove-one-from-embedded-array-to-db [collection key data]
  (mc/update database
             collection
             key
             {$pull data}))

(defn update-embedded-array-to-db [collection key data]
  (mc/update database
             collection
             key
             {$push data}))