(ns ify.loggly
  (:require [clojure.string :as string]
            [taoensso.encore :as enc]
            [cheshire.core :as json]
            [org.httpkit.client :as http]
            ))

(defn not-string? [x]
  (not (string? x)))

(defn first-or-all [x]
  (if (= (count x) 1) (first x) x))

(defn loggly-appender [& [opts]]
  (let [{:keys [token tags]} opts]
    {:enabled? true
     :async? true
     :rate-limit [[1 (enc/ms :secs 1)]]
     :output-fn #(do
                   (json/encode {:args (first-or-all (filter not-string? (:vargs %)))
                                 :level (:level %)
                                 :namespace (:?ns-str %)
                                 :timestamp (force (:timestamp_ %))
                                 :hostname (force (:hostname_ %))
                                 :message (apply str (filter string? (:vargs %)))
                                 :line (:?line %)}))
     :fn (fn [data]
           (let [{:keys [output_]} data
                 _ (println "lolo" (force output_))
                 tag (string/join "," (map name (if (nil? tags) '[:timbre] tags)))
                 url (str "http://logs-01.loggly.com/inputs/" token "/tag/" tag "/")
                 {:keys [status error body] :as res} @(http/request
                                                        {:url url
                                                         :body (force output_)
                                                         :method :post
                                                         :headers {"Content-Type" "application/json"}})]
             #_(println "loggly res: " status error body)))}))
