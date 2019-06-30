(ns ify.systems
  (:require [system.core :refer [defsystem]]
            (system.components
              [http-kit :refer [new-web-server]])
            [ify.handler :refer [app]]
            [environ.core :refer [env]]
            [com.stuartsierra.component :as component]
            [crux.api :as crux]))

(defrecord CruxDb [db]
  component/Lifecycle
  (start [component]
    (println "starting db connection")
    (let [db (crux/start-standalone-system {:kv-backend "crux.kv.rocksdb.RocksKv"
                                            :db-dir "data/db-dir-1"
                                            :event-log-dir "logs/db-dir-1"})]
      (assoc component :db db)))
  (stop [component]
    (when db
      (println "closing db connection! " db)
      (.close db)
      component)))

(defn- new-db []
  (map->CruxDb nil))

(defsystem dev-system
           [:db (new-db)
            :web (new-web-server (Integer. (env :http-port)) app)])

(defsystem base-system
           [:db (new-db)
            :web (new-web-server (Integer. (env :http-port)) app)])