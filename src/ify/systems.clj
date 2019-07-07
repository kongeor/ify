(ns ify.systems
  (:require [system.core :refer [defsystem]]
            (system.components
              [http-kit :refer [new-web-server]])
            [ify.handler :refer [app]]
            [ify.db :as db]
            [ify.spot :as spot]
            [environ.core :refer [env]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre]
            [system.repl :refer [system]]
            [crux.api :as crux]
            [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.schedule.simple :refer [schedule repeat-forever with-interval-in-minutes]]
            [clojurewerkz.quartzite.jobs :refer [defjob] :as j])
  )

;; crux

(defrecord CruxDb [db]
  component/Lifecycle
  (start [component]
    (let [db (crux/start-standalone-system {:kv-backend "crux.kv.rocksdb.RocksKv"
                                            :db-dir "data/db-dir-1"
                                            :event-log-dir "logs/db-dir-1"})]
      (timbre/info "starting crux")
      (assoc component :db db)))
  (stop [component]
    (when db
      (timbre/info "stopping crux")
      (.close db)
      component)))

(defn- new-db []
  (map->CruxDb nil))

;; quartzite

(defjob HistoryWatcher
        [ctx]
        (doseq [u (db/get-users)]
          (spot/fetch-and-persist u))
        (println "yoyoyoyoy!"))

(defn str->int [s]
  (Integer/parseInt s))

(defn schedule-history-watcher [scheduler]
  (let [job (j/build
              (j/of-type HistoryWatcher)
              (j/with-identity (j/key "jobs.history.1")))
        trigger (t/build
                  (t/with-identity (t/key "triggers.1"))
                  (t/start-now)
                  (t/with-schedule (schedule
                                     (repeat-forever)
                                     (with-interval-in-minutes (or (-> env :history-watcher-interval str->int) 30)))))]
    #_(println "yo")
    (qs/schedule scheduler job trigger)))

(defrecord Scheduler [scheduler]
  component/Lifecycle
  (start [component]
    (let [s (-> (qs/initialize) qs/start)]
      (schedule-history-watcher s)                          ;; TODO ask
      (assoc component :scheduler s)))
  (stop [component]
    (qs/shutdown scheduler)
    component))

(defn new-scheduler
  []
  (map->Scheduler {}))


(defsystem dev-system
           [:db (new-db)
            :web (new-web-server (Integer. (env :http-port)) app)
            :scheduler (new-scheduler)])

(defsystem base-system
           [:db (new-db)
            :web (new-web-server (Integer. (env :http-port)) app)
            :scheduler (new-scheduler)])