(ns rss-reader-spa.db-access
  (:require [honeysql.core :as sql]
            [clojure.java.jdbc :as jdbc]))

(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname "//localhost:5432/rssreader"
              :user "postgres"
              :password "postgres!$14"})

(def select-all-channels {:select [:channel.name
                                   :channel.url
                                   [:category.name :category]]
                          :from [:channel]
                          :join [:category [:= :channel.category_id
                                               :category.id]]})
(defn all-channels []
  (jdbc/query db-spec (sql/format select-all-channels)))
