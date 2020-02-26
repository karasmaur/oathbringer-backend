(ns oathbringer.service.example
  (:require [clojure.pprint :as pp]))

(defn request-example [req]
  (->
    (pp/pprint req)
    (str "Request Object: " req)))
