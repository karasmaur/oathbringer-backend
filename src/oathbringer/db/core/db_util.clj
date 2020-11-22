(ns oathbringer.db.core.db-util
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [monger.conversion :refer :all]
            [clojure.walk :refer :all]))

(defn connection-prod [env]
  {:dbtype (:database-type env)
   :dbname (:database-name env)
   :user (:database-username env)
   :password (:database-password env)
   :host (:database-host env)
   :port (:database-port env)})

(def connection
  {:dbtype "mongodb"
   :dbname "oathbringer"
   :user ""
   :password ""
   :host "localhost"
   :port 27017})

(def connect (mg/connect {:host (:host connection) :port (:port connection)}))

(def database
  (mg/get-db connect (:dbname connection)))

(defn save-to-db [collection data] (mc/save-and-return database collection data))

(defn find-in-db [collection query] (mc/find-one database collection query))

(defn find-all-in-db [collection query] (mc/find-maps database collection query))

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

(defn update-embedded-array-to-db-no-duplicate [collection key data]
  (mc/update database
             collection
             key
             {$addToSet data}))
