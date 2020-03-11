(ns oathbringer.repository.user-test
  (:require [clojure.test :refer :all])
  (:require [oathbringer.repository.user :refer [find-all-users create-user]]))

(deftest create-find-all-test
  (testing "find all users"
    (let [all-users (count (find-all-users))]
      (create-user {:user/firstname "Jennifer"
                :user/surname       "Watson"})
      (is (= (count (find-all-users)) (inc all-users))))))

