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

(defn get-albums []
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e]
                         :where [[e :kino.album/name ?]]})))

#_(get-albums)

(defn get-tracks []
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e]
                         :where [[e :kino.track/name ?]]})))

#_ (get-tracks)

; (def data (get-tracks))

#_(->> (group-by :kino.track/album-id data)
     (map (fn [[k v]] [k (sort (map :kino.track/number v))])))

#_(partition-by (fn [v] (or (> 3 v) (> v 6))) [1 2 3 4 5 6 7 8 ])


(defn get-plays [user-id]
  (entity-data (-> system :db :db)
               (crux/q (crux/db (-> system :db :db))
                       '{:find [e played-at]
                         :where [[e :kino.play/user-id user-id]
                                 [e :kino.play/played-at played-at]]
                         :order-by [[played-at :desc]]
                         :limit 50})))

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
       (map #(assoc-in % [:kino.play/track :kino.track/album] (get-entity (-> % :kino.play/track :kino.track/album-id))))
       (map (fn [t] (assoc-in t [:kino.play/track :kino.track/artists] (mapv get-entity (-> t :kino.play/track :kino.track/artist-ids)))))))

(comment
  (def data (get-play-data :08uc4dh5sl6f8888eydkq2sbz))

  (map #(-> % :kino.play/track :kino.track/album :kino.album/name) data))

(comment

  (defn ->part-split [pred]
    (let [a (atom nil)]
      (fn [e]
        (let [res (pred @a e)]
          #_(println res e)
          (reset! a e)
          res))))

  (->>
    (partition-by
      (->part-split (fn [p c]
                      (or (nil? p)
                          (let [v (-> p :kino.play/track :kino.track/album-id)
                                v1 (-> c :kino.play/track :kino.track/album-id)]
                               #_(println "->" v v1)
                               (= v v1)))))
      data)
    (filter #(> (count %) 1))
    #_(map #(-> % first :kino.play/track :kino.track/album))
    (map (fn [a]
           [(-> a first :kino.play/track :kino.track/album)
            (map #(-> % :kino.play/track :kino.track/number) a)]))))

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

