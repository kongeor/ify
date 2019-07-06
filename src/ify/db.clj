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

#_(get-users)

(defn get-artists []
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e]
                         :where [[e :kino.artist/name ?]]})))

#_(get-artists)

(defn get-tracks []
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e]
                         :where [[e :kino.track/name ?]]})))

(defn get-plays [user-id]
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e played-at]
                         :where [[e :kino.play/user-id user-id]
                                 [e :kino.play/played-at]]
                         :order-by [[played-at :desc]]
                         :limit 20})))

(defn get-last-play [user-id]
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e played-at]                ;; TODO ask about this
                         :where [[e :kino.play/user-id user-id]
                                 [e :kino.play/played-at played-at]]
                         :order-by [[played-at :desc]]
                         :limit 1})))

(defn get-play-data [user-id]
  (->> (get-plays user-id)
       (map #(assoc % :kino.play/track (get-entity (:kino.play/track-id %))))
       (map #(assoc-in % [:kino.play/track :kino.play/album] (get-entity (-> % :kino.play/track :kino.track/album-id))))
       (map (fn [t] (assoc-in t [:kino.play/track :kino.play/artists] (mapv get-entity (-> t :kino.play/track :kino.track/artist-ids)))))))

#_(-> system :db :db)

(def user-keys [:display_name :type])

(defn upsert-user [user refresh_token]
  (let [user-data (select-keys user user-keys)
        id (-> user :id keyword)
        user-data (assoc user-data :crux.db/id id :kino.user/refresh-token refresh_token)
        existing (get-entity id)]
    (if existing
      existing
      (do
        (crux/submit-tx
          (-> system :db :db)
          [[:crux.tx/put
            user-data]])
        user-data))))

