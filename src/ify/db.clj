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

#_(get-entity :2jkl2xJVm71azWAgZKyf42)

