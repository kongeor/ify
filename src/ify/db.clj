(ns ify.db
  (:require [crux.api :as crux]
            [system.repl :refer [system]]))

(defn- entity-data [system entities]
  (let [sys (partial (crux/db system))]
    (map #(crux/entity sys (first %)) entities)))

(defn get-artists []
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e]
                         :where [[e :type "artist"]]})))

(defn get-tracks []
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e]
                         :where [[e :type "track"]]})))
#_(get-tracks)

(defn get-entity [id]
  (let [sys (-> system :db :db)]
    (crux/entity (crux/db sys) id)))

#_(-> system :db :db)

(def user-keys [:display_name :type])

(defn upsert-user [user access_token refresh_token]
  (let [user-data (select-keys user user-keys)
        id (-> user :id keyword)
        user-data (assoc user-data :crux.db/id id :access_token access_token :refresh_token refresh_token)
        existing (get-entity id)]
    (println (pr-str user-data))
    (println (pr-str existing))
    (if existing
      existing
      (do
        (crux/submit-tx
          (-> system :db :db)
          [[:crux.tx/put
            user-data]])
        user-data))))

