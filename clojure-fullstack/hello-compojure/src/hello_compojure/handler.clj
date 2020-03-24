(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [datomic.api :as d]
            [clojure.data.json :as json]
            [ring.util.response :as resp]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:use [hiccup.core]))

; data

(def test-data
  [{:post/title "Test Post One"
    :post/tags ["c"]
    :post/type :blog
    :post/date  #inst "2018-04-13T00:00:00"
    :post/content "This is test post one. Yaaay!"}

   {:post/title "Another Post Test"
    :post/tags ["d" "prog"]
    :post/type :blog
    :post/date  #inst "2017-07-30T00:00:00"
    :post/content "Okay, this is another test post."}

   {:post/title "Termite 3D"
    :post/type :game
    :post/tags ["action" "strategy" "real-time"]
    :post/date  #inst "2017-07-30T00:00:00"
    :post/content "Super duper good game!"}

   {:post/title "Kinetic 3D"
    :post/type  :prototype
    :post/date  #inst "2017-07-30T00:00:00"
    :post/tags ["3D" "OpenGL"]
    :post/content "Very good prototype"}

   {:post/title "Mac Media Key Forwarder"
    :post/tags ["Utility"]
    :post/type  :app
    :post/date  #inst "2017-07-30T00:00:00"
    :post/content "Bestest app eva!!!"}])

; schemas

(def post-schema
  [{:db/ident :post/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The title of the post"}

   {:db/ident :post/type
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc "The type of the post (:blog :game :app :proto)"}

   {:db/ident :post/date
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc "The date of the post"}

   {:db/ident :post/tags
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/many
    :db/doc "The tags of the post"}

   {:db/ident :post/content
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The conent of the post in html"}])

; queries

(def all-posts-q
  '[:find ?e
   :where [?e :post/title]])

(def all-posts-all-data-q
  '[:find (pull ?e [:db/id :post/title :post/date :post/content :post/tags :post/type])
    :where [?e :post/title]])

(def all-posts-all-data-by-type-q
  '[:find (pull ?e [:db/id :post/title :post/date :post/content :post/tags :post/type])
    :in $ ?type
    :where
    [?e :post/type ?ptype]
    [(= ?type ?ptype)]])

; db uri

(def uri "datomic:dev://localhost:4334/hello-datomic")

(defn setup-db []
  (let [succ (d/create-database uri)]
    (if succ
      (let [conn (d/connect uri)
            db (d/db conn)]
        (let [resp (d/transact conn post-schema)]
          (println "schema creation resp" resp))))))

(defn delete-db []
  (let [succ (d/delete-database uri)]
    (println "delete" succ)))

(defn fillup-db []
  (let [conn (d/connect uri)
        db (d/db conn)
        resp (d/transact conn test-data)]
    (println "post insertion resp" resp)))

(defn get-all-posts []
  (let [conn (d/connect uri)
        db (d/db conn)
        posts (d/q all-posts-q db)]
    posts))

(defn get-all-posts-all-data []
  (let [conn (d/connect uri)
        db (d/db conn)
        posts (d/q all-posts-all-data-q db)]
    posts))

(defn get-posts-by-type [type]
  (let [dbtype (keyword type)
        conn (d/connect uri)
        db (d/db conn)
        posts (d/q all-posts-all-data-by-type-q db dbtype)]
    (sort-by :post/date (map #(assoc % :post/date (subs (pr-str (% :post/date)) 7 26)) (map first posts)))))

(defn custom-page [name]
  (html [:h1 "My First Heading"]
        [:p (str "Hello " name "!!!")]))

(get-posts-by-type "game")

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/custompage" [name] (custom-page name))
  (GET "/api-getpostsbytype" [type] (json/write-str {:posts (get-posts-by-type type)}))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:post :get]
                 :access-control-allow-credentials "true"
                 :access-control-allow-headers "Content-Type, Accept, Authorization, Authentication, If-Match, If-None-Match, If-Modified-Since, If-Unmodified-Since")
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))


