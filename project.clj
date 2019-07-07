(defproject ify "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.danielsz/system "0.4.0"]
                 [compojure "1.5.0"]
                 [environ"1.1.0"]
                 [ring/ring-defaults "0.2.0"]
                 [ring-middleware-format "0.7.0"]
                 [http-kit "2.1.19"]
                 [juxt/crux "19.06-1.1.0-alpha"]
                 [org.rocksdb/rocksdbjni "5.17.2"]
                 [clj-spotify "0.1.9"]
                 [hiccup "1.0.5"]
                 [com.taoensso/timbre "4.10.0"]
                 [clojurewerkz/quartzite "2.1.0"]]
  :plugins [[lein-environ "1.0.0"]]
  :profiles {:dev {:source-paths ["dev"]
                   :env {:http-port 3000}}
             :prod {:env {:http-port 8000
                          :repl-port 8001}
                    :dependencies [[org.clojure/tools.nrepl "0.2.12"]]}
             :uberjar {:aot :all}}
  :main ^:skip-aot ify.core
  :repl-options {:init-ns user}
  )
