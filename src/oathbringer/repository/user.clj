(ns oathbringer.repository.user
  (:require [oathbringer.db.core.db-util :refer :all]
            [nano-id.core :refer [nano-id]]
            [buddy.hashers :as hashers]))

(def user-collection "users")

(defn get-user-dto [user]
  {:external-id (:external-id user)
   :name (:name user)
   :email (:email user)
   :password (:password user)})

(defn get-user-data [user] {:external-id (nano-id 10)
                            :email (str (user :email))
                            :name (str (user :name))
                            :password (hashers/encrypt (str (user :password)))})

(defn create-user [user]
  (get-user-dto (save-to-db user-collection (get-user-data user))))

(defn update-character [user-external-id user]
  (update-to-db user-collection
                (find-oid-in-db user-collection {:external-id user-external-id})
                user))

(defn delete-user [user-external-id]
  (delete-from-db user-collection
                  (find-oid-in-db user-collection {:external-id user-external-id})))

(defn password-match? [provided-password password]
  (hashers/check password provided-password))

(defn find-user [email]
  (get-user-dto (find-in-db user-collection {:email email})))

(defn add-character-to-user [user-external-id char-id]
  (update-embedded-array-to-db user-collection
                               {:external-id user-external-id}
                               {:characters {:character_id char-id}}))

(defn remove-character-from-user [user-external-id char-id]
  (remove-one-from-embedded-array-to-db user-collection
                                        {:external-id user-external-id}
                                        {:characters {:character_id char-id}}))

(defn find-characters-ids-from-user [user-external-id]
  (:characters (find-in-db user-collection {:external-id user-external-id})))

(defn add-campaign-to-user [user-external-id campaign-id]
  (update-embedded-array-to-db user-collection
                               {:external-id user-external-id}
                               {:campaigns {:campaign_id campaign-id}}))

(defn remove-campaign-from-user [user-external-id campaign-id]
  (remove-one-from-embedded-array-to-db user-collection
                                        {:external-id user-external-id}
                                        {:campaigns {:campaign_id campaign-id}}))

(defn find-campaigns-ids-from-user [user-external-id]
  (:campaigns (find-in-db user-collection {:external-id user-external-id})))