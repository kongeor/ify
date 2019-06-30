(ns ify.handler
  (:require
    [compojure.route :as route]
    [compojure.core :refer [defroutes GET POST ANY]]
    [ring.util.response :refer [response content-type charset]]
    [ring.middleware.format :refer [wrap-restful-format]]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ify.db :as db]
    [ify.oauth :as oauth]
    [clj-spotify.core :as spotify]
    [system.repl :refer [system]]
    [ring.util.response :as response]))


(defn- handle-oauth-callback [params]
  (let [keys (oauth/get-authentication-response "foo" params)]
    (if keys
      (let [{access_token :access_token refresh_token :refresh_token} keys
            user (spotify/get-current-users-profile {} access_token)]
        (if user
          (db/upsert-user user access_token refresh_token))))))

(defroutes routes
  (GET "/" []
       (fn [{session :session}]
         (let [uid (:spot.user/id session)
               user (db/get-entity uid)
               username (or (:display_name user) "")]
           (println session)
           (str "Welcome " username "!<br><a href=\"/login\">Login</a>"))))
  (GET "/yo" []
       (db/get-artists)
       #_(->  (get-artists)
           response
           (content-type "application/json")
           (charset "UTF-8")))
  (GET "/tracks" []
       (db/get-tracks))
  (GET "/entity/:id" []
       (fn [{params :params}]
         (let [e (db/get-entity (-> params :id keyword))]
           (-> e
             response
             (content-type "application/json")
             (charset "UTF-8")))))
  (GET "/login" []
       (response/redirect (oauth/authorize-uri "foo")))
  (GET "/oauth/callback" []
       (fn [{params :params session :session}]
         (let [user (handle-oauth-callback params)
               _ (println "**" user)
               session (assoc session :spot.user/id (:crux.db/id user))]
           (->
             (response/redirect "/")
             (assoc :session session)))))
  (route/not-found "404"))

(def app
  (-> routes
    (wrap-restful-format :formats [:json])
    (wrap-defaults site-defaults)))
