(ns oathbringer.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.util.response :refer [response]]
            [ring.middleware.json :as ringJson]
            [oathbringer.service.auth :refer [rules on-error]]
            [oathbringer.service.user :refer :all]
            [oathbringer.service.character :refer :all]
            [oathbringer.service.container :refer :all]
            [oathbringer.service.item :refer :all]
            [oathbringer.service.campaign :refer :all]
            [buddy.auth.accessrules :refer (wrap-access-rules)]
            [oathbringer.middleware.error-handling :refer [wrap-error-handling]])
  (:gen-class))

(defroutes app-routes
           (context "/api" []
             (context "/user" []
               (POST "/" req (add-user-handler req))
               (POST "/login" req (user-login-handler req))
               (PUT "/" req (update-user-handler req))
               (DELETE "/" req (delete-user-handler req)))
             (context "/campaign" []
               (POST "/" req (add-campaign-handler req))
               (PUT "/:campaign-id" req (update-campaign-handler req))
               (DELETE "/:campaign-id" req (delete-campaign-handler req))
               (GET "/all" req (response (get-all-campaigns req)))
               (GET "/:campaign-id" req (response (get-campaign req)))
               (context "/:campaign-id/character" []
                 (POST "/" req (add-character-handler req))
                 (PUT "/:char-id" req (update-character-handler req))
                 (DELETE "/:char-id" req (delete-character-handler req))
                 (GET "/all" req (response (get-all-characters req)))
                 (context "/:char-id/container" []
                   (POST "/" req (add-container-handler req))
                   (PUT "/:container-id" req (update-container-handler req))
                   (DELETE "/:container-id" req (delete-container-handler req))
                   (GET "/all" req (response (get-all-containers req)))
                   (context "/:container-id/item" []
                     (POST "/" req (add-item-to-container-handler req))
                     (GET "/all" req (response (get-all-container-items-handler req))))))
               (context "/:campaign-id/item" []
                 (POST "/" req (add-item-handler req))
                 (PUT "/:item-id" req (update-item-handler req))
                 (DELETE "/:item-id" req (delete-item-handler req))
                 (GET "/all" req (response (get-all-campaign-items-handler req))))))
           (route/not-found "Error, page not found!"))

(defn main
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
