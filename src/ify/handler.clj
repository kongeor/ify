(ns ify.handler
  (:require
    [compojure.route :as route]
    [compojure.core :refer [defroutes GET POST ANY]]
    [ring.util.response :refer [response content-type charset]]
    [ring.middleware.format :refer [wrap-restful-format]]
    [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
    [ify.db :as db]
    [system.repl :refer [system]]))


(defroutes routes
  (GET "/" []
       (println (:db system))
       "Welcome!")
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
  (route/not-found "404"))

(def app
  (-> routes
    (wrap-restful-format :formats [:json])
    (wrap-defaults api-defaults)))
