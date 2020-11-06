(ns oathbringer.repository.user
  (:require [oathbringer.util.db-util :refer [save-to-db find-in-db update-embedded-array-to-db]]
            [nano-id.core :refer [nano-id]]
            [buddy.hashers :as hashers]))

(def user-collection "users")

(defn get-user-dto [user]
  {:external-id (get user "external-id")
   :name (get user "name")
   :email (get user "email")
   :password (get user "password")})

(defn get-user-data [user] {:external-id (nano-id 10)
                            :email (str (user :email))
                            :password (hashers/encrypt (str (user :password)))})

(defn create-user [user]
  (get-user-dto (save-to-db user-collection (get-user-data user))))

(defn password-match? [provided-password password]
  (hashers/check password provided-password))

(defn get-user [email]
  (get-user-dto (find-in-db user-collection {:email email})))

(defn add-character [char-external-id char-oid]
  (update-embedded-array-to-db user-collection {:external-id char-external-id} {:characters {:character_oid char-oid}}))

