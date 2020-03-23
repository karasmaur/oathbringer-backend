(ns oathbringer.repository.character-test
  (:require [clojure.test :refer :all]
            [datomic.client.api :as d]
            [oathbringer.repository.db-util :refer [conn]]
            [oathbringer.repository.character :refer :all]))

(deftest create-test
  (testing "Create one character"
    (let [db (d/with (d/with-db conn) (create-character-tx {:name "Gruvark Nahadem"}))]
      (is (= 1 (d/q find-all-chars-query db))))))

;;(run-tests)