(ns ify.spot
  (:require [clj-spotify.core :as spotify]
            [system.repl :refer [system]]
            [ify.db :as db]
            [crux.api :as crux])
  (:import (java.security MessageDigest)))

(def data
  {:items [{:track {:disc_number 1,
                    :popularity 46,
                    :duration_ms 191840,
                    :name "Hey Bulldog",
                    :explicit false,
                    :type "track",
                    :external_urls {:spotify "https://open.spotify.com/track/4epbwW20tHuF9Q6FufAn7Y"},
                    :external_ids {:isrc "GBAYE9901218"},
                    :preview_url "https://p.scdn.co/mp3-preview/42e3ebd235c1c419280b1546cb7f3381ecbcc1ea?cid=774b29d4f13844c495f206cafdad9c86",
                    :track_number 2,
                    :is_local false,
                    :id "4epbwW20tHuF9Q6FufAn7Y",
                    :available_markets [],
                    :uri "spotify:track:4epbwW20tHuF9Q6FufAn7Y",
                    :artists [{:external_urls {:spotify "https://open.spotify.com/artist/3WrFJ7ztbogyGnTHbHJFl2"},
                               :href "https://api.spotify.com/v1/artists/3WrFJ7ztbogyGnTHbHJFl2",
                               :id "3WrFJ7ztbogyGnTHbHJFl2",
                               :name "The Beatles",
                               :type "artist",
                               :uri "spotify:artist:3WrFJ7ztbogyGnTHbHJFl2"}],
                    :album {:album_type "album",
                            :release_date "2014-01-01",
                            :images [{:height 640,
                                      :url "https://i.scdn.co/image/e95106a376a6160de871e8d32bf31232eaebfbfa",
                                      :width 640}
                                     {:height 300,
                                      :url "https://i.scdn.co/image/098e8402efb8c75b132aff1c3c8afdd81be4e1c7",
                                      :width 300}
                                     {:height 64,
                                      :url "https://i.scdn.co/image/6a3f57206d1a1f8282c054a0dcb0b465a4417e7d",
                                      :width 64}],
                            :name "Yellow Submarine Songtrack",
                            :release_date_precision "day",
                            :type "album",
                            :external_urls {:spotify "https://open.spotify.com/album/0XRZpF083HqgygM0v1hQyE"},
                            :id "0XRZpF083HqgygM0v1hQyE",
                            :available_markets [],
                            :uri "spotify:album:0XRZpF083HqgygM0v1hQyE",
                            :artists [{:external_urls {:spotify "https://open.spotify.com/artist/3WrFJ7ztbogyGnTHbHJFl2"},
                                       :href "https://api.spotify.com/v1/artists/3WrFJ7ztbogyGnTHbHJFl2",
                                       :id "3WrFJ7ztbogyGnTHbHJFl2",
                                       :name "The Beatles",
                                       :type "artist",
                                       :uri "spotify:artist:3WrFJ7ztbogyGnTHbHJFl2"}],
                            :total_tracks 15,
                            :href "https://api.spotify.com/v1/albums/0XRZpF083HqgygM0v1hQyE"},
                    :href "https://api.spotify.com/v1/tracks/4epbwW20tHuF9Q6FufAn7Y"},
            :played_at "2019-06-29T06:25:22.067Z",
            :context nil}
           {:track {:disc_number 1,
                    :popularity 29,
                    :duration_ms 296213,
                    :name "Хочу перемен",
                    :explicit false,
                    :type "track",
                    :external_urls {:spotify "https://open.spotify.com/track/6L5iRhYgVPaEFqmGaVxWrN"},
                    :external_ids {:isrc "FR59R1744876"},
                    :preview_url "https://p.scdn.co/mp3-preview/858863f6906865e0e66b694c277a3648c0be819d?cid=774b29d4f13844c495f206cafdad9c86",
                    :track_number 1,
                    :is_local false,
                    :id "6L5iRhYgVPaEFqmGaVxWrN",
                    :available_markets [],
                    :uri "spotify:track:6L5iRhYgVPaEFqmGaVxWrN",
                    :artists [{:external_urls {:spotify "https://open.spotify.com/artist/2jkl2xJVm71azWAgZKyf42"},
                               :href "https://api.spotify.com/v1/artists/2jkl2xJVm71azWAgZKyf42",
                               :id "2jkl2xJVm71azWAgZKyf42",
                               :name "Kino",
                               :type "artist",
                               :uri "spotify:artist:2jkl2xJVm71azWAgZKyf42"}
                              {:external_urls {:spotify "https://open.spotify.com/artist/4dngB44yGo3ErubxhE1bUW"},
                               :href "https://api.spotify.com/v1/artists/4dngB44yGo3ErubxhE1bUW",
                               :id "4dngB44yGo3ErubxhE1bUW",
                               :name "Viktor Tsoi",
                               :type "artist",
                               :uri "spotify:artist:4dngB44yGo3ErubxhE1bUW"}],
                    :album {:album_type "album",
                            :release_date "2017-06-21",
                            :images [{:height 640,
                                      :url "https://i.scdn.co/image/9dd125147045fbebefe9ecbe313cf19c84b0139c",
                                      :width 640}
                                     {:height 300,
                                      :url "https://i.scdn.co/image/df2d075eed4fccabc83b5e53ec30eb8fd80a5995",
                                      :width 300}
                                     {:height 64,
                                      :url "https://i.scdn.co/image/871947c9223cc5faeb676a97b6d0ede9a262289a",
                                      :width 64}],
                            :name "Виктор Цой 55 (Выпуск в честь 55-летия Виктора Цоя)",
                            :release_date_precision "day",
                            :type "album",
                            :external_urls {:spotify "https://open.spotify.com/album/7trila5XMOsUUkcujWqzcn"},
                            :id "7trila5XMOsUUkcujWqzcn",
                            :available_markets [],
                            :uri "spotify:album:7trila5XMOsUUkcujWqzcn",
                            :artists [{:external_urls {:spotify "https://open.spotify.com/artist/2jkl2xJVm71azWAgZKyf42"},
                                       :href "https://api.spotify.com/v1/artists/2jkl2xJVm71azWAgZKyf42",
                                       :id "2jkl2xJVm71azWAgZKyf42",
                                       :name "Kino",
                                       :type "artist",
                                       :uri "spotify:artist:2jkl2xJVm71azWAgZKyf42"}
                                      {:external_urls {:spotify "https://open.spotify.com/artist/4dngB44yGo3ErubxhE1bUW"},
                                       :href "https://api.spotify.com/v1/artists/4dngB44yGo3ErubxhE1bUW",
                                       :id "4dngB44yGo3ErubxhE1bUW",
                                       :name "Viktor Tsoi",
                                       :type "artist",
                                       :uri "spotify:artist:4dngB44yGo3ErubxhE1bUW"}],
                            :total_tracks 55,
                            :href "https://api.spotify.com/v1/albums/7trila5XMOsUUkcujWqzcn"},
                    :href "https://api.spotify.com/v1/tracks/6L5iRhYgVPaEFqmGaVxWrN"},
            :played_at "2019-06-29T06:21:59.772Z",
            :context {:uri "spotify:album:7trila5XMOsUUkcujWqzcn",
                      :external_urls {:spotify "https://open.spotify.com/album/7trila5XMOsUUkcujWqzcn"},
                      :href "https://api.spotify.com/v1/albums/7trila5XMOsUUkcujWqzcn",
                      :type "album"}}],
   :next "https://api.spotify.com/v1/me/player/recently-played?before=1561789319772&limit=2",
   :cursors {:after "1561789522067", :before "1561789319772"},
   :limit 2,
   :href "https://api.spotify.com/v1/me/player/recently-played?limit=2"})


(def track-keys [:explicit :name :track_number :type])

(def track (-> data :items first :track))

(defn track-artist-ids [track]
  (into #{} (map #(-> % :id keyword) (:artists track))))

(defn track-album-id [track]
  (-> track :album :id keyword))

#_(track-album-id track)

#_(track-artist-ids track)

(defn get-tracks [data]
  (mapv #(let [track (:track %)
               t (select-keys track track-keys)
               t (assoc t :crux.db/id (-> track :id keyword))
               t (assoc t :album_id (track-album-id track))
               t (assoc t :artist_ids (track-artist-ids track))]
           (assoc {}
             :crux.db/id (-> track :id keyword)
             :kino.track/album-id (track-album-id track)
             :kino.track/artist-ids (track-artist-ids track)
             :kino.track/explicit (:explicit t)
             :kino.track/number (:track_number t)
             :kino.track/name (:name t))) (-> data :items)))

;; 1
#_(get-tracks data)

(defn md5 [^String s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        raw (.digest algorithm (.getBytes s))]
    (format "%032x" (BigInteger. 1 raw))))

(defn get-user-plays [uid data]
  (let [d (:items data)]
    (map #(let [played_at (-> % :played_at clojure.instant/read-instant-date)
                track_id (-> % :track :id keyword)]
            (println (str (name uid) (name track_id) (inst-ms played_at)))
            {:crux.db/id (keyword (md5 (str (name uid) (name track_id) (inst-ms played_at))))
             :kino.play/user-id uid
             :kino.play/track-id track_id
             :kino.play/played-at played_at
             ;:type "play"
             }) d)))

(def user-keys [:display_name :type])

(defn get-user-data [user-data]
  (let [u (select-keys user-data user-keys)]
    (assoc u :crux.db/id (-> user-data :id keyword))))

#_(get-user-data user-data)

;; 2
#_(get-user-plays :asdf data)

(def artist-keys [:name :type])

(defn get-artists [data]
  (apply concat
         (map (fn [items]
                (let [artists (-> items :track :album :artists)]
                  (mapv (fn [artist]
                          (let [a (select-keys artist artist-keys)]
                            (assoc {} :crux.db/id (-> artist :id keyword)
                                      :kino.artist/name (:name a)))) artists)))
              (:items data))))

#_(-> data :items first :track :album :artists)

;; 3
#_(get-artists data)

(def album-keys [:name :release_date :type :images :total_tracks])

(defn get-albums [data]
  (map (fn [items]
         (let [album (-> items :track :album)]
           (let [a (select-keys album album-keys)]
             (assoc {} :crux.db/id (-> album :id keyword)   ;; TODO no need for assoc
                      :kino.album/name (:name a)
                      :kino.album/release-date (:release_date a)
                      :kino.album/images (:images a)
                      :kino.album/total-tracks (:total_tracks a)))))
       (:items data)))

;; 4
#_(get-albums data)

(defn get-all-the-things [uid data]
  (let [tracks (get-tracks data)
        plays (get-user-plays uid data)
        artists (get-artists data)
        albums (get-albums data)]
    (concat tracks plays artists albums)))


(defn prepare-for-tx [data]
  (mapv (fn [d]
         [:crux.tx/put
          d]) data))

#_(get-all-the-things :asdf #_(-> user-data get-user-data :crux.db/id) data)

#_(prepare-for-tx
  (get-all-the-things (-> user-data get-user-data :crux.db/id) data))

#_(-> system :db :db)

(defn persist-all-data [uid data]
  (crux/submit-tx
    (-> system :db :db)
    (prepare-for-tx
      (get-all-the-things uid data))))


(defn- get-data-after [data played-at]
  (let [played-at-milis (inst-ms played-at)]
    (println "filtering by play time " played-at-milis)
    (assoc data
      :items
      (filter #(> (-> % :played_at clojure.instant/read-instant-date inst-ms)
                  played-at-milis) (:items data)))))

(defn fetch-and-persist [{id :crux.db/id refresh-token :kino.user/refresh-token}]
  ;; TODO filter out existing
  (let [data (spotify/get-current-users-recently-played-tracks {:limit 50} access_token)
        _ (println "** -> " (-> data :items count) "user " id)
        {played_at :played_at} (first (db/get-last-play id))
        filtered (if played_at (get-data-after data played_at) data)]
    (println "persisting filtered " (-> filtered :items count) " for user " id)
    (persist-all-data id filtered)))

#_(let [{played_at :played_at} (first (db/get-last-play :08uc4dh5sl6f8888eydkq2sbz))]
  (println played_at))