(ns oathbringer.repository.user-test
  (:require [clojure.test :refer :all])
  (:require [oathbringer.repository.user :refer [find-all create]]))

(deftest create-find-all-test
  (testing "find all users"
    (let [all-users (count (find-all))]
      (create  {:user/firstname "Jennifer"
                :user/surname "Watson"})
      (is (= (count (find-all)) (inc all-users))))))
