(ns oathbringer.util.db-util
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]))

(def connect (mg/connect {:host "localhost" :port 27017}))

(def database
  (mg/get-db connect "oathbringer"))

(defn save-to-db [collection data] (mc/save-and-return database collection data))

(defn find-in-db [collection query] (mc/find-one database collection query))

(defn find-oid [collection query] (get (mc/find-one database collection query) "_id"))

(defn update-to-db [collection oid data]
  (mc/update-by-id database
                   collection
                   oid
                   data))

(defn update-embedded-array-to-db [collection key data]
  (mc/update database
             collection
             key
             {$push data}))