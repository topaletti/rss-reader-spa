(ns rss-reader-spa.prod
  (:require [rss-reader-spa.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
