(ns rss-reader-spa.download
  (:require [feedparser-clj.core :as rss]))

(defn channel-with-entries [channel]
  "Downloads RSS file and adds entries to channel map."
  (->> (rss/parse-feed (:url channel))
       (:entries)
       (into [])
       (assoc channel :entries)))
