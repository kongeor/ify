(ns ify.db
  (:require [crux.api :as crux]
            [system.repl :refer [system]]))

(defn get-entity [id]
  (let [sys (-> system :db :db)]
    (crux/entity (crux/db sys) id)))

(defn- entity-data [system entities]
  (let [sys (partial (crux/db system))]
    (map #(crux/entity sys (first %)) entities)))

(defn get-users []
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e]
                         :where [[e :type "user"]]})))

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

(defn get-plays [user-id]
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e played-at]
                         :where [[e :type "play"]
                                 [e :user_id user-id]
                                 [e :played_at played-at]]
                         :order-by [[played-at :desc]]
                         :limit 20})))

(defn get-last-play [user-id]
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e played-at]                ;; TODO ask about this
                         :where [[e :type "play"]
                                 [e :user_id user-id]
                                 [e :played_at played-at]]
                         :order-by [[played-at :desc]]
                         :limit 1})))

(defn get-play-data [user-id]
  (->> (get-plays user-id)
       (map #(assoc % :track (get-entity (:track_id %))))
       (map #(assoc-in % [:track :album] (get-entity (-> % :track :album_id))))
       (map (fn [t] (assoc-in t [:track :artists] (mapv get-entity (-> t :track :artist_ids)))))))

#_(-> system :db :db)

(def user-keys [:display_name :type])

(defn upsert-user [user access_token refresh_token]
  (let [user-data (select-keys user user-keys)
        id (-> user :id keyword)
        user-data (assoc user-data :crux.db/id id :access_token access_token :refresh_token refresh_token)
        existing (get-entity id)]
    (println (pr-str user-data))
    (println (pr-str existing))
    (crux/submit-tx
      (-> system :db :db)
      [[:crux.tx/put
        user-data]])
    user-data))

