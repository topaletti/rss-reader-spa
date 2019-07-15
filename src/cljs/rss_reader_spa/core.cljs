(ns rss-reader-spa.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]))

(def categories
  (atom [{:name "Technology"
          :channels [{:name "Slashdot"
                      :url "http://rss.slashdot.org/Slashdot/slashdotMain"
                      :entries [{:title "A Linux article"
                                 :desc "Linux is good stuff."}
                                {:title "An Apple article"
                                 :desc "b"}
                                {:title "A Google article"
                                 :desc "c"}]}]}
         {:name "World News"
          :channels [{:name "Reuters: World"
                      :url "http://feeds.reuters.com/Reuters/worldNews"
                      :entries [{:title "Trump"
                                 :desc "d"}
                                {:title "Russian hackers"
                                 :desc "e"}
                                {:title "North Korean missiles"
                                 :desc "f"}]}
                     {:name "Deutsche Welle: Europe"
                      :url "http://rss.dw.com/atom/rss-en-eu"
                      :entries [{:title "Merkel tired of it all"
                                 :desc "g"}
                                {:title "Opinion: Mean people are mean"
                                 :desc "h"}
                                {:title "Brexit - this time for real, maybe"
                                 :desc "i"}
                                {:title (str "Extremely long title that should "
                                             "span over multiple lines "
                                             "(at least on my screen) "
                                             "just to show what it looks like")
                                 :desc "Description."}]}
                     {:name "Channel"
                      :url "http://rss.example.com/feed"
                      :entries (into []
                                     (repeat 20 {:title "Title"
                                                 :desc "Description."}))}]}]))

(def entry-atom (atom {:title "Title goes here"
                       :desc "Description goes here."}))

(defn refresh-all-channels []
  )

(defn add-category []
  )

(defn add-channel []
  )

(defn save-configuration []
  )

(defn menu []
  [:div#menu
   [:button {:on-click refresh-all-channels} "Refresh all channels"]
   #_[:button {:on-click add-category} "Add category"]
   #_[:button {:on-click add-channel} "Add channel"]
   #_[:button {:on-click save-configuration} "Save configuration"]])

(defn category-display []
  [:div
   (map (fn [category]
          ^{:key category} [:div
           [:h3 (:name category)]
           (map (fn [channel]
                  ^{:key channel} [:div
                   [:h4 (:name channel)]
                   [:div.entry-list
                    (map (fn [entry]
                           ^{:key entry} [:p
                            [:a {:on-click #(reset! entry-atom entry)}
                                           (:title entry)]])                         
                         (:entries channel))]])
                (:channels category))])
        @categories)])

;; clojure.lang.ArityException:
;; Wrong number of args (3) passed to: cljs.core/for
#_(defn category-display []
  [:div
   (for [category @categories]
     ^{:key category} [:h3 (:name category)]
     (for [channel (:channels category)]
       ^{:key channel} [:h4 (:name channel)]
       (for [entry (:entries channel)]
         ^{:key entry} [:p
          [:a {:on-click #(reset! entry-atom entry)} (:title entry)]])))])

(defn entry-display []
  [:div
   [:h2 (:title @entry-atom)]
   [:p (:desc @entry-atom)]])

(defn home-page []
  [:div
   [menu]
   [:div.main
    [:div#category-display [category-display]]
    [:div#entry-display [entry-display]]]])

(def router
  (reitit/router
   [["/" :index]
    ["refresh-all" :refresh-all]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

(defn page-for [route]
  (case route
    :index #'home-page
    :refresh-all #'home-page))

(defn current-page []
  (let [page (:current-page (session/get :route))]
    [page]))

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)
        ))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))
