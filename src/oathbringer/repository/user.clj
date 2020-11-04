(ns oathbringer.repository.user
  (:require [oathbringer.util.db-util :refer [save-to-db find-in-db]]
            [nano-id.core :refer [nano-id]]
            [buddy.hashers :as hashers]))

(def collection "users")

(defn get-user-dto [user]
  "Returns a map with the user's fields"
  {:external-id (get user "external-id")
   :name (get user "name")
   :email (get user "email")
   :password (get user "password")})

(defn get-user-data [user] {:external-id (nano-id 10)
                            :email (str (user :email))
                            :password (hashers/encrypt (str (user :password)))})

(defn create-user [user]
  "Returns the user after transacting it to datomic"
  (save-to-db collection (get-user-data user)))

(defn password-match? [provided-password password]
  "Check to see if the password given matches the digest of the user's saved password"
  (hashers/check password provided-password))

(defn get-user [email]
  (get-user-dto (find-in-db collection {:email email})))

