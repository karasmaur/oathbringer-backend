(ns oathbringer.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.util.response :refer [response]]
            [ring.middleware.json :as ringJson]
            [oathbringer.service.user :refer [add-user-handler get-all-users-handler user-login-handler]]
            [oathbringer.service.auth :refer [backend rules on-error]]
            [oathbringer.service.example :refer [request-example]]
            [buddy.auth.accessrules :refer (wrap-access-rules)])
  (:gen-class))

(defroutes app-routes
           (context "/api" []
             (context "/user" []
               (POST "/" req (response (add-user-handler req)) )
               (GET "/" req (response (get-all-users-handler req)))
               (POST "/login" req (user-login-handler req)))
             (GET "/request" req (response (request-example req))))
           (route/not-found "Error, page not found!"))

(defn -main
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (server/run-server (-> #'app-routes
                           (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
                           (wrap-access-rules {:rules rules :on-error on-error})
                           (ringJson/wrap-json-params)
                           (ringJson/wrap-json-response)) {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
