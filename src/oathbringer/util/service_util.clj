(ns oathbringer.util.service-util
  (:require [clojure.data.json :as json]))

(defn response-payload [status body]
  "Returns a map accepted by the http server. To be used on response with json objects as body."
  {:status  status
   :headers {"Content-Type" "text/json"}
   :body    (json/write-str body)})

(defn get-parameter [req pname] (get (:params req) pname))