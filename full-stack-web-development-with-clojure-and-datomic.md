# Full stack web develoment with clojure and datomic

## Table of contents
* Prerequisites
* Preface
* Setting up the server
* Serving a static html page
* Generating html on the server
* Generating html on the client
* Setting up the client
* Creating a single page application
* Setting up datomic
* Creating the database
* Deploying to a server


## Prerequisites

You should be familiar with [clojure](clojure-by-examples.md) and you should have a code editor with a clojure plugin for inline evaluation. You should be familiar with command line execution and terminals, you will need at least three terminal windows or four if you use a terminal-based editor. I recommend [linux](https://github.com/milgra/tutorials/blob/master/pretty-and-functional-linux-destkop.md) for clojure development.

## Preface

Simply put, full stack web development is generating web pages for the user's browser based on the user's requests. This can happen in the browser on the client side and on the web server machine on the server side, usually both side is involved.

A web server is a program that listens for connections from browsers and based on the route and the parameters of the request it passes back data. This data can be a web page written in HTML or any kind of data that the client side code will insert into the existing client side HTML page.

Example URL ( Uniform Resource Locator ) :

https://example.com/user/1134?showage=false&showhair=false

the server address is : [https://example.com] 

the route is : [/user/1134]

the parameters are : [showage=false&showhair=false]

## Setting up the server

Create a compojure project template with leiningen ( install it first if you don't have it ) :

```lein new compojure hello-compojure```

go to the project folder and start it up :

```lein ring server-headless```

it will start up and tells us the port where we can reach the server

```Started server on port 3000```

So let's go into a browser and type the URL

```localhost:3000```

And we should see a page telling us "Hello World"

Good so far, let's start an nrepl server along with the web server so we can do inline evaluation from our code editor on the server code.

Open the project in your editor, open project.clj, and add nrepl to the dependencies :

```
:dependencies [[org.clojure/clojure "1.10.0"]
               [org.clojure/tools.nrepl "0.2.13"]
               [compojure "1.6.1"]
               [ring/ring-defaults "0.3.2"]]
```

and add a start flag to the ring part also

```
:ring {:handler hello-compojure.handler/app
       :nrepl {:start? true}}
```

Now stop the server with CTRL-C and start it again.
Along with the server port it now tells us the nrepl port :

```Started nREPL server on port 46725```

Connect your editor's nrepl plugin to this port. In emacs you should open the minibuffer with ALT+X, enter ```cider-connect-clj``` for host type ```localhost``` , for port type the resulting port from the previous command.

To check if inline eval is working open ```src/hello_compojure/core.clj``` in your editor and insert this somewhere :

```(+ 3 2)```

place the cursor after the closing bracket and evaluate the expression ( in emacs press CTRL+X+E )

the result should show up immediately

```(+ 3 2) => 5```

now let's check if the server updates if we modify something. modify the app-routes function like this :

```
(defroutes app-routes
  (GET "/" [] "Hello World---")
  (route/not-found "Not Found"))
```

save the file and reload the page in the browser. the text in the browser should change to ```Hello World---```

so we are set up for development

## Serving a static html page

the simplest task a web server can do is to serve a static html page. let's try this

create a new file under ```resources/public``` called ```index.html``` with the following content

```
<!DOCTYPE html>
<html>
<body>
<h1>My First Heading</h1>
<p>My first paragraph.</p>
</body>
</html>
```
then we have to tell the server to serve resources from root folder. modify ```app-routes``` function in ```core.clj``` :

```
(defroutes app-routes
  (GET "/" [] "Hello World---")
  (route/resources "/")
  (route/not-found "Not Found"))
```

now go into the browser and type ```localhost:3000/index.html``` and the previously created ```index.html``` shows up. so we just served a static resource to the client's browser!

a static resource can be any kind of a file but mainly images that a web site uses

and how do we tell the server to show index.html without adding ```/index.html``` to the address to act as the main page of our site?

we have to use the ```ring.util.respponse``` namespace for that. update the require list at the top of the file like this :

```
(:require [compojure.core :refer :all]
          [compojure.route :as route]
          [ring.util.response :as resp]
          [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))
```

and modify app-routes also :

```
(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (route/resources "/")
  (route/not-found "Not Found"))
```

so if you open the browser and go to ```localhost:3000``` the index.html will show up because we redirected the root route to it!

## Generating html on the server

let's see now how to generate html pages dynamically on the server.

create a function in ```core.clj``` called ```custom-page``` over ```app-routes```

```
(defn custom-page []
  "Custom page"
  )
```

it's quite simple for now. now call it from anywhere in the code and evaluate it to check if it works properly

```(custom-page) => "Custom page"```

it returns the string so it looks good. now let's connect it to a route. modify ```app-routes```

```
(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/custompage" [] (custom-page))
  (route/resources "/")
  (route/not-found "Not Found"))
```

if you check ```localhost:3000/custompage``` in the browser the same string will show up in the browser!!! yaaay!!!

now we will generate super complex html structures in the code with the help of hiccup library 

add the latest hiccup library to the project's dependencies in project.clj

```
:dependencies [[org.clojure/clojure "1.10.0"]
               [org.clojure/tools.nrepl "0.2.13"]
               [compojure "1.6.1"]
               [hiccup "1.0.5"]
               [ring/ring-defaults "0.3.2"]]
```
and restart the server

in core.clj let's use the entire hiccup namespace 

```
(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:use [hiccup.core]))
```

and we can start using hiccup notation to generate html. let's re-create index.html in the code with hiccup in the ```custom-page``` function

```
(defn custom-page [name]
  (html [:h1 "My First Heading"]
        [:p (str "Hello " name "!!!")]))
```

check it in the browser, you should see the same result as before

the only problem that this looks still too static! so let's add some twist here, the page will accept a name as parameter and we will insert the name in the generated page.

modfy the ```custom page``` function to accept a name parameter and generate a greeting text for the name :

```
(defn custom-page [name]
  (html [:html
         [:body
          [:h1 "My First Heading"]
          [:p (str "Hello " name	]]))
```

now modify ```app-routes``` to accept a name parameter

```
(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/custompage" [name] (custom-page name))
  (route/resources "/")
  (route/not-found "Not Found"))
```

now go into the browser and open the following url :

```http://localhost:3000/custompage?name=Milan```

and a greeting text will show up under the header.

and that's how you create dynamic content on the server.

## Generating html on the client

generating content on the server and loading it to the browser all the time with every small change is not that dynamic. for a super dynamic and bandwidth-effective experience you have to generate the page on the client-side based on the data that you request from the server when it is needed.

## Setting up the client

we will use shadow-cljs for clojurescript development. it is a tool that provides a repl and connects it to the live code running in the browser so you can update/evaluate functions in the running code and make inline evaluation and development easier. install it first if you don't have it

we will base our client side code on reagent, which is a wrapper for the javascript library react. it contains additional useful things for clojurescript development.

create a shadow-cljs project template with reagent :

```lein new shadow-cljs hello-reagent +reagent```

go into the created folder and install npm dependencies :

```npm install```

start watching the app with shadow :

```shadow-cljs watch app```

after a succesful start shadow tells you the port of the web server where you can reach the resulting html and the port of the nrepl server :

```
shadow-cljs - HTTP server available at http://localhost:8700
shadow-cljs - nREPL server started on port 8777
```

so if you go into your browser and enter ```localhost:8700``` you will see a Hello world message showing up from your reagent template

you should also connect to the repl from your editor. 

based on your editor you may have to add a dependency first to the project. in case of emacs/cider add this to ```:dependencies``` part in ```shadow-cljs.edn``` :

```[cider/cider-nrepl "0.24.0"]```

then restart watching with ```shadow-cljs watch app```

then connect your editor to the repl. in emacs ALT+X, cider-connect-cljs, enter localhost, the port, select shadow for repl type, :app for build

carefully read what cider outputs in the nrepl buffer, if it has a problem, for example mismatching cider-nrepl version number it will tell you and you have to modify versions accordingly

now that everything is set up let's work on the code. let's modify something and hot-swap in the running page

edit ```src/hello_reagent/core.cljs``` and modify the hello-world function :

```
(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "It should change!"]])
```

and save the file. the page in the browser should update automagically to the new content.

and we are ready for action!

## Creating a single page application

we will create a simple but complex enough application to showcase datomic, server-side apis, reagent and css.  we will build up the basics of ```milgra.com```, the four cards containing apps, protos, games and blogs.

delete ```hello-world``` function and create a ```page``` function that will be the main component of our app :

```
(defn page []
  [:div
   [:div "ONE"]
   [:div "TWO"]
   [:div "THREE"]
   [:div "FOUR"]])
```

and tell reagent in function ```start``` to render this component instead of ```hello-world```

``` 
(defn start []
  (reagent/render-component [page]
                            (. js/document (getElementById "app"))))
```

if you check out the actual state of the page in the browser it's just four labels under each other. how do we make vertical cards out of them? the answer is css power, and we can add it inline with hiccup!

```
(defn page []
  [:div
   [:div
    {:style {:position "absolute"
             :width "200px"
             :left "200px"
             :background "#AAFFAA"
             :min-height "100vh"}}
    "ONE"]
   [:div
    {:style {:position "absolute"
             :width "200px"
             :left "300px"
             :background "#FFFFAA"
             :min-height "100vh"}}
    "TWO"]
   [:div
    {:style {:position "absolute"
             :width "200px"
             :left "400px"
             :background "#AAFFFF"
             :min-height "100vh"}}
    "THREE"]
   [:div
    {:style {:position "absolute"
             :width "400px"
             :left "500px"
             :background "#FFAAFF"
             :min-height "100vh"}}
    "FOUR"]])
```

the page now looks better but isn't it repetitive a little bit? is there a way the make it less repetitive? the answer is programming!!!

let's create a data structure first to store the individual properties of each card

```
(def carddata [{:x "200px" :w "200px" :col "#AAFFAA" :txt "ONE"}
               {:x "300px" :w "200px" :col "#FFFFAA" :txt "TWO"}
               {:x "400px" :w "200px" :col "#AAFFFF" :txt "THREE"}
               {:x "500px" :w "400px" :col "#FFAAFF" :txt "FOUR"}])
```

let's create the function that generates a card based on a data structure item

```
(defn card [ data ]
  [:div
   {:style {:position "absolute"
            :width (:w data)
            :left (:x data)
            :background (:col data)
            :min-height "100vh"}}
   (:txt data)])
```
and finally add component generation to the page component

```
(defn page []
  [:div
   (map card carddata)])
```

and now we want the clicked one move to the center. we will use reagent's ability to auto-update components where a shared reagent atom is used.

first we will have to be able to rearrange cards so we make an atom called cardlist containing the unique parts of ```carddata``` and two another vectors containing fixed card widths and poisitions

```
(defonce cardlist (atom [{:col "#AAFFAA" :txt "ONE"}
                         {:col "#FFFFAA" :txt "TWO"}
                         {:col "#AAFFFF" :txt "THREE"}
                         {:col "#FFAAFF" :txt "FOUR"}]))

(defonce cardpos ["200px" "300px" "400px" "500px"])
(defonce cardwth ["200px" "200px" "200px" "400px"])
```

we will have to modify the iteration in ```page``` function sightly, we want to pass an indexed item to ```card``` function so it knows which position and width element to use for rendering the actual card. ```map-indexed vector``` pairs the indexes with elements in cardlist.

```
(defn page []
  [:div
   (map card (map-indexed vector @cardlist))])
```

we also have to slighyl modify ```card``` function and add click event

```
(defn card [ [ index data ] ]
  (let [txt (:txt data)]
    [:div
     {:style {:position "absolute"
              :width (nth cardwth index)
              :left (nth cardpos index)
              :background (:col data)
              :min-height "100vh"}
      :on-click (fn [e]
                  ; shift menuitems
                  (reset! cardlist
                          (concat
                          (filter #(not= (% :txt) txt) @cardlist)
                          (filter #(= (% :txt) txt) @cardlist))))}
     (:txt data)]))
```

and now the selected card jumps to the middle and the others shift left. splendid!

but we have an annoyinh warning in the developer console of the browser :

```
Warning: Every element in a seq should have a unique :key:
```

and that's because react needs a unique id for every component to speed up state changes. so let's add a key to our pages

so let's add a key to each card to fix this 

```
(defn card [ [ index data ] ]
  (let [txt (:txt data)]
    [:div
     {:key (str "card" index)
      :style {:position "absolute"
              :width (nth cardwth index)
              :left (nth cardpos index)
              :background (:col data)
              :min-height "100vh"}
      :on-click (fn [e]
                  ; shift menuitems
                  (reset! cardlist
                          (concat
                          (filter #(not= (% :txt) txt) @cardlist)
                          (filter #(= (% :txt) txt) @cardlist))))}
     (:txt data)]))
```

the only thing I don't like is the little gap between the top and the cards, let's fix this with css, edit ```public/css/style.css``` :

```
body {
    margin : 0px;
}
```

so we have four dynamically positioned cards crying for content! this is the time for datomic and a server side api!

## Setting up datomic

request a license for the starter version of datomic on-prem from [datomic](https://www.datomic.com/get-datomic.html)

after that go to [https://my.datomic.com/account](https://my.datomic.com/account)

there will be the wget link for the full distribution, download it

at the bottom of the page there is an ```or via leiningen``` section. copy the first three lines and create a file

```~/.lein/credentials.clj```

and paste the lines into it

download and install gnupg for your system and create a default key with ```gpg --gen-key```

now you can encrypt the previously created credentials.clj with gpg :

```gpg --default-recipient-self -e ~/.lein/credentials.clj > ~/.lein/credentials.clj.gpg```

and now leiningen can download the peer library automagically for datomic

go back to our server project, hello-compojure and edit project.clj. you have to add a ```:repositories``` part and a datomic dependency

```
:repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                 :creds :gpg}}
:dependencies [[org.clojure/clojure "1.10.0"]
               [org.clojure/tools.nrepl "0.2.13"]
               [com.datomic/datomic-pro "0.9.6024"]
               [compojure "1.6.1"]
               [hiccup "1.0.5"]
               [ring/ring-defaults "0.3.2"]]
```

your datomic-pro version might differ, use the correct dependency version!

before starting up datomic you have to select a transactor properties file and add your license key to it

go to datomic folder and copy the dev-transactor-template.properties file to the root folder

```cp /config/samples/dev-transactor-template.properties dev.properties```

then edit dev.properties add add the license key after ```license-key``` part that your received via email from datomic

and now we are ready to start up datomic. at the root folder of datomic type

```bin/transactor dev.properties```

then, in another terminal, go to the root folder of hello-compojure and start up the project

```lein ring server-headless```

if everything goes well, lein downloads the datomic peer library and starts up

## Creating the database

we are going to edit ```handler.clj``` under src/hello_compojure. require datomic peer lib first

```
(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [datomic.api :as d]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
```

bind the uri

```
(def uri "datomic:dev://localhost:4334/hello-datomic")
```

create a setup-db function

```
(defn setup-db []
  (let [succ (d/create-database uri)
        conn (d/connect uri)]
  (println "creation" succ "connection" conn)))

```

re-send the file to the repl or restart the server

then evaluate the inner ( ```let``` ) part of the function inline, the repl buffer should show a successful creation and a connection info map

we should create the ```delete-db``` function also to speed up development, we will delete and re-schema and re-fill the database all the time

```
(defn delete-db []
  (let [succ (d/delete-database uri)]
    (println "deletion" succ)))
```

evaluate the inner part, deletion should print ```true``` to the repl, then try evaluating the inner part of ```setup-db``` again and creation should print ```true```

now we are ready to create our schema

we will store posts with four possible types for the four cards. they will have title, content, date, type and tags.


```
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
```

and let's add schema creation to ```setup``` function

```
(defn setup-db []
  (let [succ (d/create-database uri)]
    (if succ
	    (let [conn (d/connect uri)
	          db (d/db conn)]
        (let [resp (d/transact conn post-schema)]
          (println "schema creation resp" resp))))))
```

here we guard schema creation with the successful database creation because if creation fails then the db is already created and schema is already created

delete the db and evaluate setup-db again. if something fails remember to send the whole file to the repl or send the newly created parts to the repl individually with inline evaluation so every definition is visible for newly created functions

no we are ready to fill up the database. create a test-data structure

```
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
```
let's create a fillup-db function to pass this data to datomic

```
(defn fillup-db []
  (let [conn (d/connect uri)
        db (d/db conn)
        resp (d/transact conn test-data)]
    (println "post insertion resp" resp)))
```
evaluate the inner part and the test data should be in the database

and now... query time!!!

let's get all posts first with a title ( probably all posts because every post should have a title )

```
(def all-posts-q
  '[:find ?e
   :where [?e :post/title]])
   
(defn get-all-posts []
  (let [conn (d/connect uri)
        db (d/db conn)
        posts (d/q all-posts-q db)]
    posts))
```
if you evaluate the insode of ```get-all-posts``` you should get a list of numbers. these numbers are entity id's of the inserted entities. if you want to get all fields of an entity you can do ```(d/entity db entityid)``` and to put the result back to a map similar to the insertion map you can do ```(into {} (seq (d/entity db entityid)))```

but we can also modify the query to pass back all values of all entities in a vector, we will use the pull pattern

```
(def all-posts-all-data-q
  '[:find (pull ?e [:db/id :post/title :post/date :post/content :post/tags :post/type])
    :where [?e :post/title]])
    
(defn get-all-posts-all-data []
  (let [conn (d/connect uri)
        db (d/db conn)
        posts (d/q all-posts-all-data-q db)]
    posts))
```

if you evaluate the inner part of ```get-all-posts-all-data``` all fields for all entities should show up as result

we created the previous functions to aid our development, we can check what's inside the db or are they still inside, now let's create a query that we will actually use. let's request entities based on their type to request them for our cards in the browser

```
(def all-posts-all-data-by-type-q
  '[:find (pull ?e [:db/id :post/title :post/date :post/content :post/tags :post/type])
    :in $ ?type
    :where
    [?e :post/type ?ptype]
    [(= ?type ?ptype)]])
```

the query accepts an input variable, the type which is a keyword in this case and check's if the current post/type is similar to this input varibale

the function which uses this query :

```
(defn get-posts-for-type [type]
  (let [dbtype (keyword type)
        conn (d/connect uri)
        db (d/db conn)
        posts (d/q all-posts-all-data-by-type-q db dbtype)]
    (sort-by :post/date (map #(assoc % :post/date (subs (pr-str (% :post/date)) 7 26)) (map first posts)))))
```

after getting the result we convert the #inst timestamp to a form more suitable for client-server data exchange and finally we sort the result by date

let's test it, evaluate this function :

```
(get-posts-for-type "game")
```

now we are ready to push this data through an api call to the client side. we will use json for communication between the client and the server. add the dependency to project.clj first

```
:dependencies [[org.clojure/clojure "1.10.0"]
               [org.clojure/tools.nrepl "0.2.13"]
               [com.datomic/datomic-pro "0.9.6024"]
               [org.clojure/data.json "0.2.6"]
               [compojure "1.6.1"]
               [hiccup "1.0.5"]
               [ring/ring-defaults "0.3.2"]]
```

then require json in handler.clj

```
(:require [compojure.core :refer :all]
          [compojure.route :as route]
          [datomic.api :as d]
          [clojure.data.json :as json]
          [ring.util.response :as resp]
          [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
```

add a new route to ```app-routes```

```
(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/custompage" [name] (custom-page name))
  (GET "/api-getpostsbytype" [type] (json/write-str {:posts (get-posts-by-type type)}))
  (route/resources "/")
  (route/not-found "Not Found"))
```

save the file, restart the server and test the new route in the browser. open ```localhost:3000/api-getpostbytype?type=game```

if everything is fine let's go to the client side and the client project and request posts from the server

let's go to client side.

## Loading content with the server side API

we are going back to ```hello-reagent/src/hello_reagent/core.clj```

let's modify the card labels to apps, games, protos and blog and add a type property to them

```
(defonce cardlist (atom [{:col "#AAFFAA" :txt "APPS" :type "app"}
                         {:col "#FFFFAA" :txt "GAMES" :type "game"}
                         {:col "#AAFFFF" :txt "PROTOS" :type "proto"}
                         {:col "#FFAAFF" :txt "BLOG" :type "blog"}]))
```
we will also need a new reagent atom for strogin the received posts

```
(defonce content (atom nil))
```
we will need ```cljs-http``` library. add to the dependencies in ```shadow-cljs.edn```

```
 :dependencies [[binaryage/devtools "0.9.7"]
                [cljs-http "0.1.45"]
                [reagent "0.9.1"]
                [markdown-to-hiccup "0.6.2"]
                [cider/cider-nrepl "0.24.0"]
                [reanimated "0.6.1"]]
```

and require it in ```core.cljs``` . we also have to require clojure.core.async for async operations

```
(ns hello-reagent.core
  (:require [reagent.core :as reagent :refer [atom]]
  	    [clojure.core.async :as async]
            [cljs-http.client :as http]))
```

and now restart shadow-cljs

let's create the function that requests posts from the server-side API

```
(defn get-posts-by-type [type]
  (async/go
    (let [{:keys [status body]} (async/<! (http/get (str server-url "/api-getpostsbytype")
                                                    {:query-params {:type type}}))
          result (js->clj (.parse js/JSON body) :keywordize-keys true)
          posts (reverse (result :posts))]
      (println "posts" posts)
      (reset! content posts))))
```

let's call this function from the top of the ```card``` function when the index is 4, so only the active card will load it's content

```
(defn card [ [ index data ] ]
  (if (= index 3)
    (get-posts-by-type (:type data)))
  (let [txt (:txt data)]
    [:div
     {:key (str "card" index)
      :style {:position "absolute"
              :width (nth cardwth index)
              :left (nth cardpos index)
              :background (:col data)
              :min-height "100vh"}
      :on-click (fn [e]
                  ; shift menuitems
                  (reset! cardlist
                          (concat
                          (filter #(not= (% :txt) txt) @cardlist)
                          (filter #(= (% :txt) txt) @cardlist))))}
     (:txt data)]))
```

and we are ready to roll!!! let's try it in the browser

hmm, something is wrong, check the developer console of the browser :

```
Access to XMLHttpRequest at 'http://localhost:3000/api-getpostsbytype?type=blog' from origin 'http://localhost:8700' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

we are blocked by a server-side policy! whoa!


if you want to get 

so we now have two webservers running on our machine, one for ring/compojure from the previous examples and one for shadow-cljs development/evaluation, we also have two nrepl ports, one for ring/compojure development and one for the client-side development and we have two separate projects! let's merge at least the project to simplify things.

## Deploying to a server

## Summary

so that's what full stack web development is about. you add and request data to/from a database through a server with api calls and resource requests, and you maintain states in a single page web application and stylize it with css. hope you liked the tutorial and you will stick with clojure! 



to see how to create a more complex app using markdown to store post, reanimated for css animations, check out milgra.com github project


check DEBUG mode, auto url switching
port 80 when release mode for auto port switching







create pager, update css

create menus for all page, request data from server, mock server responses

create database connection, show how to test server inline

use data returned from server on the client side

use a href's for url browsability & search engine optimization

deploy the whole stuff to hetzner

markdown html contnet

a href's

todo : history

database connection

shadow cljs - web server 8700, repl
datomic db 4334
datomic console 8080
ring server headless - 3000

deploy to serever

hetzner.com - ssh
install java 8
memory limit
port set

