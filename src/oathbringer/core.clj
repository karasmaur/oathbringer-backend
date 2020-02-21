(ns oathbringer.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [ring.util.response :refer [response]]
            [ring.middleware.json :as ringJson]
            [oathbringer.datomic-access :refer [transact-user get-all-users]])
  (:gen-class))

(defn simple-body-page [req] ;(3)
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello World"})

(defn request-example [req]
     {:status  200
      :headers {"Content-Type" "text/html"}
      :body    (->
                (pp/pprint req)
                (str "Request Object: " req))})

(defn user-handler [req]
        (response (get-all-users)))

; Helper to get the parameter specified by pname from :params object in req
(defn getparameter [req pname] (get (:params req) pname))

(defn addperson-handler [req]
  (-> (let [p (partial getparameter req)]
        (response (transact-user {:user/firstname (str (p :firstname))
                                  :user/surname  (str (p :surname))} )))))

(defroutes app-routes
           (POST "/api/user" [] addperson-handler)
           (GET "/api/user" [] user-handler)
           (GET "/" [] simple-body-page)
           (GET "/request" [] request-example)
           (route/not-found "Error, page not found!"))

(defn -main
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (server/run-server (-> #'app-routes
                           (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
                           (ringJson/wrap-json-params)
                           (ringJson/wrap-json-response)) {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
