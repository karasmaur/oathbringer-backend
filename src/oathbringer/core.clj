(ns oathbringer.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.util.response :refer [response]]
            [ring.middleware.json :as ringJson]
            [oathbringer.service.user :refer [add-user-handler user-login-handler]]
            [oathbringer.service.character :refer [add-character-handler
                                                   update-character-handler
                                                   get-all-characters
                                                   delete-character-handler]]
            [oathbringer.service.auth :refer [rules on-error]]
            [buddy.auth.accessrules :refer (wrap-access-rules)]
            [oathbringer.middleware.error-handling :refer [wrap-error-handling]])
  (:gen-class))

(defroutes app-routes
           (context "/api" []
             (context "/user" []
               (POST "/" req (add-user-handler req))
               (POST "/login" req (user-login-handler req)))
             (context "/character" []
               (POST "/" req (add-character-handler req))
               (PUT "/" req (update-character-handler req))
               (DELETE "/" req (delete-character-handler req))
               (GET "/all" req (response (get-all-characters req)))))
           (route/not-found "Error, page not found!"))

(defn -main
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (server/run-server (-> #'app-routes
                           (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
                           (wrap-access-rules {:rules rules :on-error on-error})
                           (ringJson/wrap-json-params)
                           (ringJson/wrap-json-response)
                           ;;(wrap-error-handling)
                           )
                       {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
