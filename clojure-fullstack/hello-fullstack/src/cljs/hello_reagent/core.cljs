(ns hello-reagent.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.core.async :as async]
            [cljs-http.client :as http]))

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defonce cardlist (atom [{:col "#AAFFAA" :txt "APPS" :type "app"}
                         {:col "#FFFFAA" :txt "GAMES" :type "game"}
                         {:col "#AAFFFF" :txt "PROTOS" :type "prototype"}
                         {:col "#FFAAFF" :txt "BLOG" :type "blog"}]))

(def cardpos ["200px" "300px" "400px" "500px"])
(def cardwth ["100px" "100px" "100px" "400px"])
(def server-url (if js/goog.DEBUG "http://localhost:3000" "http://your.server.ip.address"))

(defonce content (atom nil))

(defn get-posts-by-type [type]
  (async/go
    (let [{:keys [status body]} (async/<! (http/get (str server-url "/api-getpostsbytype")
                                                    {:query-params {:type type}}))
          result (js->clj (.parse js/JSON body) :keywordize-keys true)
          posts (reverse (result :posts))]
      (println "posts" posts)
      (reset! content posts))))


(defn posts []
  (if @content
    [:div
     (map (fn [post]
            [:div {:key (rand 1000000)}
             [:h1 (post :title)]                 
             [:h2 (str (clojure.string/replace (post :date) #"T" " ") " / " (clojure.string/join "," (post :tags)))]
             [:br]
             (post :content)
             [:br]
             [:hr]])
          @content)]))


(defn card [ [ index data ] ]
  (if (= index 3)
    (get-posts-by-type (:type data)))
  (let [txt (:txt data)]
    [:div
     {:key (str "card" index)
      :class "card"
      :style {:width (nth cardwth index)
              :left (nth cardpos index)
              :background (:col data)}
      :on-click (fn [e]
                  ; shift menuitems
                  (reset! cardlist
                          (concat 
                          (filter #(not= (% :txt) txt) @cardlist)
                          (filter #(= (% :txt) txt) @cardlist))))}
     [:div {:class ["verticaltext cardbutton"]} (:txt data)]
     (if (= index 3)
       [posts])]))


(defn page []
  [:div
   (doall (map card (map-indexed vector @cardlist)))])

(defn start []
  (reagent/render-component [page]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))
