# Full stack web develoment with clojure and datomic

## Table of contents
* Prerequisites
* Preface
* Setting up the server
* Serving a static html page
* Generating html on the server
* Generating html on the client
* Creating a single page application
* Setting up datomic
* Creating the database
* Loading content with the server side API
* Merging the two projects together
* Deploying to a server
* Summary

## Prerequisites

You should be familiar with [clojure](clojure-by-examples.md) and you should have a code editor with a clojure plugin for inline evaluation. You should be familiar with command line execution and terminals, you will need at least three terminal windows or four if you use a terminal-based editor. I recommend [linux](https://github.com/milgra/tutorials/blob/master/pretty-and-functional-linux-destkop.md) for clojure development.

You can download/browse the three projects created in this tutorial here : [https://github.com/milgra/tutorials/tree/master/clojure-fullstack](https://github.com/milgra/tutorials/tree/master/clojure-fullstack)

## Preface

Simply put, full stack web development is generating web pages for the user's browser based on the user's requests. This can happen in the browser on the client side and on the web server machine on the server side, usually both side is involved.

A web server is a program that listens for connections from browsers and based on the route and the parameters of the request it passes back data. This data can be a web page written in HTML or any kind of data that the client side code will insert into the existing client side HTML page.

Example URL ( Uniform Resource Locator ) :

https://example.com/user/1134?showage=false&showhair=false

The server address is : [https://example.com] 

The route is : [/user/1134]

The parameters are : [showage=false&showhair=false]

## Setting up the server

Create a compojure project template with leiningen ( install it first if you don't have it ).

```lein new compojure hello-compojure```

Go to the project folder and start it up.

```lein ring server-headless```

It will start up and tells us the port where we can reach the server.

```Started server on port 3000```

So let's go into a browser and type the URL.

```localhost:3000```

And we should see a page telling us "Hello World".

Good so far, let's start an nrepl server along with the web server so we can do inline evaluation from our code editor on the server code.

Open the project in your editor, open project.clj, and add nrepl to the dependencies.

```
:dependencies [[org.clojure/clojure "1.10.0"]
               [org.clojure/tools.nrepl "0.2.13"]
               [compojure "1.6.1"]
               [ring/ring-defaults "0.3.2"]]
```

And add a start flag to the ring part also.

```
:ring {:handler hello-compojure.handler/app
       :nrepl {:start? true}}
```

Now stop the server with CTRL-C and start it again. Along with the server port it now tells us the nrepl port.

```Started nREPL server on port 46725```

Connect your editor's nrepl plugin to this port. In emacs you should open the minibuffer with ALT+X, enter ```cider-connect-clj``` for host type ```localhost``` , for port type the resulting port from the previous command.

To check if inline eval is working open ```src/hello_compojure/core.clj``` in your editor and insert this somewhere.

```(+ 3 2)```

Place the cursor after the closing bracket and evaluate the expression ( in emacs press CTRL+X+E ).

The result should show up immediately.

```(+ 3 2) => 5```

Now let's check if the server updates if we modify something. modify the app-routes function like this.

```
(defroutes app-routes
  (GET "/" [] "Hello World---")
  (route/not-found "Not Found"))
```

Save the file and reload the page in the browser. the text in the browser should change to ```Hello World---```.

So we are set up for development.

## Serving a static html page

The simplest task a web server can do is to serve a static html page. let's try this.

Create a new file under ```resources/public``` called ```index.html``` with the following content.

```
<!DOCTYPE html>
<html>
<body>
<h1>My First Heading</h1>
<p>My first paragraph.</p>
</body>
</html>
```
Then we have to tell the server to serve resources from root of the public folder. Modify ```app-routes``` function in ```core.clj```.

```
(defroutes app-routes
  (GET "/" [] "Hello World---")
  (route/resources "/")
  (route/not-found "Not Found"))
```

Now go into the browser and type ```localhost:3000/index.html``` and the previously created ```index.html``` shows up. So we just served a static resource to the client's browser!

A static resource can be any kind of a file but they are mainly images and other media files that a web site uses.

And how do we tell the server to show index.html without adding ```/index.html``` to the address to act as the main page of our site?

We have to use the ```ring.util.response``` namespace for that. Update the requirements at the top of the file like this.

```
(:require [compojure.core :refer :all]
          [compojure.route :as route]
          [ring.util.response :as resp]
          [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))
```

And modify app-routes also.

```
(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (route/resources "/")
  (route/not-found "Not Found"))
```

So if you open the browser and go to ```localhost:3000``` the index.html will show up because we redirected the root route to it!

## Generating html on the server

Let's see now how to generate html pages dynamically on the server.

Create a function in ```core.clj``` called ```custom-page``` over ```app-routes```.

```
(defn custom-page []
  "Custom page"
  )
```

It's quite simple for now. Now call it from anywhere in the code and evaluate it to check if it works properly.

```(custom-page) => "Custom page"```

It returns the string so it looks good. Now let's connect it to a route. Modify ```app-routes```.

```
(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/custompage" [] (custom-page))
  (route/resources "/")
  (route/not-found "Not Found"))
```

If you check ```localhost:3000/custompage``` in the browser the same string will show up in the browser!!! Yaaay!!!

Now we will generate super complex html structures in the code with the help of hiccup library.

Add the latest hiccup library to the project's dependencies in project.clj.

```
:dependencies [[org.clojure/clojure "1.10.0"]
               [org.clojure/tools.nrepl "0.2.13"]
               [compojure "1.6.1"]
               [hiccup "1.0.5"]
               [ring/ring-defaults "0.3.2"]]
```
And restart the server.

In core.clj let's use the entire hiccup namespace. 

```
(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:use [hiccup.core]))
```

And we can start using hiccup notation to generate html. Let's re-create index.html in the code with hiccup in the ```custom-page``` function.

```
(defn custom-page [name]
  (html [:h1 "My First Heading"]
        [:p (str "Hello " name "!!!")]))
```

Check it in the browser, you should see the same result as before.

The only problem that this looks still too static! So let's add some twist here, the page will accept a name as parameter and we will insert the name in the generated page.

Modfy the ```custom page``` function to accept a name parameter and generate a greeting text for the name.

```
(defn custom-page [name]
  (html [:html
         [:body
          [:h1 "My First Heading"]
          [:p (str "Hello " name	]]))
```

Now modify ```app-routes``` to accept a name parameter.

```
(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/custompage" [name] (custom-page name))
  (route/resources "/")
  (route/not-found "Not Found"))
```

Now go into the browser and open the following url.

```http://localhost:3000/custompage?name=Milan```

And a greeting text will show up under the header.

And that's how you create dynamic content on the server.

## Generating html on the client

Generating content on the server and loading it to the browser all the time with every small change is not really dynamic. For a super dynamic and bandwidth-effective experience you have to generate the page on the client-side based on the data that you request from the server when it is needed.

We will use shadow-cljs for clojurescript development. It is a tool that provides a repl and connects it to the live code running in the browser so you can update/evaluate functions in the running code and make inline evaluation and development easier. Install it first if you don't have it.

We will base our client side code on reagent which is a wrapper for the javascript library react. It contains additional useful things for clojurescript development.

Create a shadow-cljs project template with reagent.

```lein new shadow-cljs hello-reagent +reagent```

Go into the created folder and install npm dependencies.

```npm install```

Start watching the app with shadow.

```shadow-cljs watch app```

After a succesful start shadow tells you the port of the web server where you can reach the resulting html and the port of the nrepl server.

```
shadow-cljs - HTTP server available at http://localhost:8700
shadow-cljs - nREPL server started on port 8777
```

So if you go into your browser and enter ```localhost:8700``` you will see a Hello world message showing up from your reagent template.

You should also connect to the repl from your editor. 

Based on your editor you may have to add a dependency first to the project. In case of emacs/cider add this to ```:dependencies``` part in ```shadow-cljs.edn``` :

```[cider/cider-nrepl "0.24.0"]```

Then restart watching with ```shadow-cljs watch app```.

Then connect your editor to the repl. In emacs ALT+X, cider-connect-cljs, enter localhost, the port, select shadow for repl type, :app for build.

Carefully read what cider outputs in the nrepl buffer, if it has a problem, for example mismatching cider-nrepl version number it will tell you and you have to modify versions accordingly!

Now that everything is set up let's work on the code. Let's modify something and hot-swap in the running page.

Edit ```src/hello_reagent/core.cljs``` and modify the hello-world function.

```
(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "It should change!"]])
```

Save the file. The page in the browser should update automagically to the new content.

And we are ready for action!

## Creating a single page application

We will create a simple but complex enough application to showcase datomic, server-side apis, reagent and css. We will build up the basics of ```milgra.com```, the four cards containing apps, protos, games and blogs.

Delete ```hello-world``` function and create a ```page``` function that will be the main component of our app.

```
(defn page []
  [:div
   [:div "ONE"]
   [:div "TWO"]
   [:div "THREE"]
   [:div "FOUR"]])
```

And tell reagent in function ```start``` to render this component instead of ```hello-world```.

``` 
(defn start []
  (reagent/render-component [page]
                            (. js/document (getElementById "app"))))
```

If you check out the actual state of the page in the browser it's just four labels under each other. How do we make vertical cards out of them? The answer is css power, and we can add it inline with hiccup!

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

The page now looks better but isn't it repetitive a little bit? Is there a way the make it less repetitive? The answer is programming!!!

Let's create a data structure first to store the individual properties of each card.

```
(def carddata [{:x "200px" :w "200px" :col "#AAFFAA" :txt "ONE"}
               {:x "300px" :w "200px" :col "#FFFFAA" :txt "TWO"}
               {:x "400px" :w "200px" :col "#AAFFFF" :txt "THREE"}
               {:x "500px" :w "400px" :col "#FFAAFF" :txt "FOUR"}])
```

Let's create a function that generates a card based on a data structure item.

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
And finally add component generation to the page component.

```
(defn page []
  [:div
   (map card carddata)])
```

And now we want the clicked one move to the center. We will use reagent's ability to auto-update components where a shared reagent atom is used.

First we will have to be able to rearrange cards so we make an atom called cardlist containing the unique parts of ```carddata``` and two another vectors containing fixed card widths and poisitions

```
(defonce cardlist (atom [{:col "#AAFFAA" :txt "ONE"}
                         {:col "#FFFFAA" :txt "TWO"}
                         {:col "#AAFFFF" :txt "THREE"}
                         {:col "#FFAAFF" :txt "FOUR"}]))

(defonce cardpos ["200px" "300px" "400px" "500px"])
(defonce cardwth ["100px" "100px" "100px" "400px"])
```

We will have to modify the iteration in ```page``` function sightly, we want to pass an indexed item to ```card``` function so it knows which position and width to use for rendering the actual card. ```map-indexed vector``` pairs the indexes with elements in cardlist.

```
(defn page []
  [:div
   (map card (map-indexed vector @cardlist))])
```

We also have to slighyl modify ```card``` function and add a click event.

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

And now the selected card jumps to the middle and the others shift left. Splendid!

But we have an annoyinh warning in the developer console of the browser!

```
Warning: Every element in a seq should have a unique :key:
```

And that's because react needs a unique id for every component to speed up state changes. So let's add a key to each card to fix this. 

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

Good, but there's another warning for react not handling lazy-seqs very well, so let's modify ```page``` function to fix that also with a forced evaluation with doall.

```
(defn page []
  [:div
   (doall (map card (map-indexed vector @cardlist)))])
```

The only thing I don't like is the little gap between the top and the cards, let's fix this with css, edit ```public/css/style.css```.

```
body {
    margin : 0px;
}
```

So we have four dynamically positioned cards crying for content! This is the time for datomic and a server side api!

## Setting up datomic

Request a license for the starter version of datomic on-prem from [datomic](https://www.datomic.com/get-datomic.html).

After that go to [https://my.datomic.com/account](https://my.datomic.com/account), there will be the wget link for the full distribution, download and unzip it.

At the bottom of the page there is an ```or via leiningen``` section. Copy the first three lines and create a file at
```~/.lein/credentials.clj``` and paste the lines into it.

Download and install gnupg for your system and create a default key with ```gpg --gen-key```.

Now you can encrypt the previously created credentials.clj with gpg.

```gpg --default-recipient-self -e ~/.lein/credentials.clj > ~/.lein/credentials.clj.gpg```

And now leiningen can download the peer library automagically for datomic.

Go back to the server project hello-compojure and edit project.clj. You have to add a ```:repositories``` part and a datomic dependency.

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

Your datomic-pro version might differ, use the correct dependency version!

Before starting up datomic you have to select a transactor properties file and add your license key to it.

Go to datomic folder and copy the dev-transactor-template.properties file to the root folder.

```cp /config/samples/dev-transactor-template.properties dev.properties```

Then edit dev.properties add add the license key after ```license-key``` part that your received via email from datomic.

And now we are ready to start up datomic. Go to the root folder and type

```bin/transactor dev.properties```

Then, in another terminal, go to the root folder of hello-compojure and start up the project.

```lein ring server-headless```

If everything goes well, lein downloads the datomic peer library and starts up.

## Creating the database

We are going to edit ```handler.clj``` under src/hello_compojure. Require datomic peer lib first.

```
(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [datomic.api :as d]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
```

Bind the uri.

```
(def uri "datomic:dev://localhost:4334/hello-datomic")
```

Create a setup-db function.

```
(defn setup-db []
  (let [succ (d/create-database uri)
        conn (d/connect uri)]
  (println "creation" succ "connection" conn)))

```

Re-send the file to the repl or restart the server, then evaluate the inner ( ```let``` ) part of the function inline, the repl buffer should show a successful creation and a connection info map.

We should create the ```delete-db``` function also to speed up development, we will delete and re-schema and re-fill the database all the time.

```
(defn delete-db []
  (let [succ (d/delete-database uri)]
    (println "deletion" succ)))
```

Evaluate the inner part, deletion should print ```true``` to the repl, then try evaluating the inner part of ```setup-db``` again and creation should print ```true```.

Now we are ready to create our schema.

We will store posts with four possible types for the four cards. They will have title, content, date, type and tags.

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

And let's add schema creation to the ```setup``` function.

```
(defn setup-db []
  (let [succ (d/create-database uri)]
    (if succ
	    (let [conn (d/connect uri)
	          db (d/db conn)]
        (let [resp (d/transact conn post-schema)]
          (println "schema creation resp" resp))))))
```

Here we guard schema creation with the successful database creation because if creation fails then the db is already created and schema is already created.

Delete the db and evaluate setup-db again. If something fails remember to send the whole file to the repl or send the newly created parts to the repl individually with inline evaluation so every definition is visible for newly created functions.

Now we are ready to fill up the database. Create a test-data structure.

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
Let's create a fillup-db function to pass this data to datomic.

```
(defn fillup-db []
  (let [conn (d/connect uri)
        db (d/db conn)
        resp (d/transact conn test-data)]
    (println "post insertion resp" resp)))
```
Evaluate the inner part and the test data should be in the database.

And now... Query time!!!

Let's get all posts first with a title ( probably all posts because every post should have a title ).

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
If you evaluate the inside of ```get-all-posts``` you should get a list of numbers. These numbers are entity id's of the inserted entities. If you want to get all fields of an entity you can do ```(d/entity db entityid)``` and to put the result back to a map similar to the insertion map you can do ```(into {} (seq (d/entity db entityid)))```.

But we can also modify the query to pass back all values of all entities in a vector, we will use the pull pattern.

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

If you evaluate the inner part of ```get-all-posts-all-data``` all fields for all entities should show up as result.

We created the previous functions to aid our development, to check what's inside the db or are they still inside, now let's create a query that we will actually use. Let's request entities based on their type to request them for our cards in the browser.

```
(def all-posts-all-data-by-type-q
  '[:find (pull ?e [:db/id :post/title :post/date :post/content :post/tags :post/type])
    :in $ ?type
    :where
    [?e :post/type ?ptype]
    [(= ?type ?ptype)]])
```

The query accepts an input variable, the type which is a keyword in this case and check's if the current post/type is similar to this input varibale.

The function which uses this query :

```
(defn get-posts-for-type [type]
  (let [dbtype (keyword type)
        conn (d/connect uri)
        db (d/db conn)
        posts (d/q all-posts-all-data-by-type-q db dbtype)]
    (sort-by :post/date (map #(assoc % :post/date (subs (pr-str (% :post/date)) 7 26)) (map first posts)))))
```

After getting the result we convert the #inst timestamp to a form more suitable for json data exchange and finally we sort the result by date.

Let's test it, evaluate this function :

```
(get-posts-for-type "game")
```

Now we are ready to push this data through an api call to the client side. We will use json for communication between the client and the server. Add the dependency to project.clj first.

```
:dependencies [[org.clojure/clojure "1.10.0"]
               [org.clojure/tools.nrepl "0.2.13"]
               [com.datomic/datomic-pro "0.9.6024"]
               [org.clojure/data.json "0.2.6"]
               [compojure "1.6.1"]
               [hiccup "1.0.5"]
               [ring/ring-defaults "0.3.2"]]
```

Then require json in handler.clj.

```
(:require [compojure.core :refer :all]
          [compojure.route :as route]
          [datomic.api :as d]
          [clojure.data.json :as json]
          [ring.util.response :as resp]
          [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
```

Add a new route to ```app-routes```.

```
(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/custompage" [name] (custom-page name))
  (GET "/api-getpostsbytype" [type] (json/write-str {:posts (get-posts-by-type type)}))
  (route/resources "/")
  (route/not-found "Not Found"))
```

Save the file, restart the server and test the new route in the browser. open ```localhost:3000/api-getpostbytype?type=game```.

If everything is fine let's go to the client side and the client project and request posts from the server.

## Loading content with the server side API

We are going back to ```hello-reagent/src/hello_reagent/core.clj```.

Let's modify the card labels to apps, games, protos and blog and add a type property to them.

```
(defonce cardlist (atom [{:col "#AAFFAA" :txt "APPS" :type "app"}
                         {:col "#FFFFAA" :txt "GAMES" :type "game"}
                         {:col "#AAFFFF" :txt "PROTOS" :type "prototype"}
                         {:col "#FFAAFF" :txt "BLOG" :type "blog"}]))
```
We will also need a new reagent atom for strogin the received posts.

```
(defonce content (atom nil))
```
We will need ```cljs-http``` library. Add to the dependencies in ```shadow-cljs.edn```.

```
 :dependencies [[binaryage/devtools "0.9.7"]
                [cljs-http "0.1.45"]
                [reagent "0.9.1"]
                [markdown-to-hiccup "0.6.2"]
                [cider/cider-nrepl "0.24.0"]
                [reanimated "0.6.1"]]
```

And require it in ```core.cljs``` . We also have to require clojure.core.async for async operations.

```
(ns hello-reagent.core
  (:require [reagent.core :as reagent :refer [atom]]
  	    [clojure.core.async :as async]
            [cljs-http.client :as http]))
```

And now restart shadow-cljs.

Let's create the function that requests posts from the server-side API.

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

Let's call this function from the top of the ```card``` function when the index is 3, so only the active card will load it's content. We will also show the content as string if index is 3.

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
     (:txt data)
     (if (= index 3)
       (str @content))
     ]))
```

And we are ready to roll!!! Let's try it in the browser.

Hmm, something is wrong, check the developer console of the browser.

```
Access to XMLHttpRequest at 'http://localhost:3000/api-getpostsbytype?type=blog' from origin 'http://localhost:8700' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

We are blocked by a server-side policy! Whoa!

Add ring-cors dependency to hello-compojure in project.clj.

```
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 [com.datomic/datomic-pro "0.9.6024"]
                 [org.clojure/data.json "0.2.6"]
                 [ring-cors "0.1.13"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [ring/ring-defaults "0.3.2"]]
```

Require it in handler.clj in the server.

```
(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [datomic.api :as d]
            [clojure.data.json :as json]
            [ring.util.response :as resp]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
```
And finally replace app definition in handler.clj with this :

```
(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:post :get]
                 :access-control-allow-credentials "true"
                 :access-control-allow-headers "Content-Type, Accept, Authorization, Authentication, If-Match, If-None-Match, If-Modified-Since, If-Unmodified-Since")
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))
```

And restart the server ( ```lein ring server-headless``` ).

And now if you load the site in the browser the posts should show up in the third card. Yaay!!! 

Super cool, let's pimp it up a little bit, let's create a component that contains the posts.

```
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
```

So with map we iterate through all posts and render it's attributes with different html elements. Let's add posts component to card.

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
     (:txt data)
     (if (= index 3)
       [posts])]))
```

Check it in the browser!

Cool, it's getting ready now but it's super ugly! Let's do some css styling!

Edit stlye.css in the client.

Vertical labels for the cards :

```
.verticaltext {
    top : -5px;
    font-size : 27px;
    transform : rotate(-90deg);
    transform-origin : bottom right;
}

.cardbutton {
    cursor : pointer;
    position : absolute;
    right : 20px;
    display : inline-block;
}
```
Let's add the label in a separate div to card component so we can use these class selectors on it.

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
     [:div {:class ["verticaltext cardbutton"]} (:txt data)]
     (if (= index 3)
       [posts])]))
```

Let's add better looking fonts and a little shadow to style.css.

```
body {
    margin : 0px;

    text-shadow: 1px 1px 3px #AAAAAA;
    text-align: justify;

    font-size : 18px;
    font-family : -apple-system,BlinkMacSystemFont,Avenir,Avenir Next,Segoe UI,Roboto,Oxygen,Ubuntu,Cantarell,Fira Sans,Droid Sans,Helvetica Neue,sans-serif;
}

```
And modify header element styles a little.

```
h1 {
    font-size : 27px;
    font-weight : normal;
}

h2 {
    font-size : 14px;
    font-weight : normal;
}
```

Now it's getting better!!! A little margin around the content would be awesome and also we should wire out non-changing card styles from the code. Add this to style.css :

```
.card {
    position : absolute;
    min-height : 100vh;
    padding : 10px;
}
```

And modify card again.

```
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
```

Cool, now our page is working well and looking good. We should deploy it to a server but first we should merge these two project into one project to make development simpler.

## Merging the two projects into one project

So far we have two webservers running on our machine, one for ring/compojure from the server-side and one for shadow-cljs development/evaluation, we also have two nrepl ports, one for ring/compojure development and one for the client-side development and we have two separate projects! Let's merge at least the project to simplify things.

Since shadow-cljs uses shadow-cljs.edn and lein uses project.clj for project setup we can easily merge the two projects with only a few modifications.

Create a new folder called ```hello-fullstack```.

Copy ```hello-compojure/project.clj``` to ```hello-fullstack/project.clk```

Copy ```hello-compojure/src/hello_compojure/``` to ```hello-fullstack/src/clj/hello_compojure/```

Copy ```hello-reagent/package.json``` to ```hello-fullstack/package.json```

Copy ```hello-reagent/shadow-cljs.edn``` to ```hello-fullstack/shadow-cljs.edn```

Copy ```hello-reagent/src/hello_reagent/``` to ```hello-fullstack/src/cljs/hello_reagent/```

So we create a separate clj folder for server-side code, and a cljs folder for client-side code under src.
 
Copy ```hello-reagent/public``` to ```hello-fullstack/resources/public```

And now we have to modify ```hello-fullstack/shadow-cljs.edn``` for the updated sources and resources location.

```
;; shadow-cljs configuration
{:source-paths ["src/cljs"]

 :dependencies [[binaryage/devtools "0.9.7"]
                [cider/cider-nrepl "0.25.0-SNAPSHOT"]
                [cljs-http "0.1.45"]
                [reagent "0.8.0-alpha2"]]

 ;; set an nrepl port for connection to a REPL.
 :nrepl        {:port 8777}

 :builds
 {:app {:target :browser
        :output-dir "resources/public/js/compiled"
        :asset-path "/js/compiled"

        :modules
        {:main
         {:entries [hello-reagent.core]}}

        :devtools
        ;; before live-reloading any code call this function
        {:before-load hello-reagent.core/stop
         ;; after live-reloading finishes call this function
         :after-load hello-reagent.core/start
         ;; serve the public directory over http at port 8700
         :http-root    "resources/public"
         :http-port    8700
         :preloads     [devtools.preload]}}}}
```

In ```hello-fullstack/project.clj``` we have to add the new source-path location.

```
(defproject hello-compojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 [com.datomic/datomic-pro "0.9.6024"]
                 [org.clojure/data.json "0.2.6"]
                 [ring-cors "0.1.13"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [ring/ring-defaults "0.3.2"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler hello-compojure.handler/app
         :nrepl {:start? true}}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
```

And now close all terminals and editors, we will do a fresh start with everything for our newly created project

Start datomic first with ```bin/transactor dev.properties```

Then start the server, go to ```hello-fullstack``` and type ```lein ring headless```

Then download npm dependencies with ```npm install```

Then start shadow with ```shadow-cljs watch app```

If everything went fine check the page in the browser ```localhost:8700```

And the cool thing is, since we merged the two project and pointed resources folder to the same folder, the freshly generated shadow-cljs output is also reachable by our compojure server, and since we added a redirect for index.html it can also serve our client-side code like it will do in production.

So check out that also at ```localhost:3000```

You should see the same on both addresses.

Cool, now development/project structure/version control improved big time! the only thing left is deployment!!!

## Deploying to a server

To deploy to a server first you need a server! :) I recommend [https://www.hetzner.com/cloud](https://www.hetzner.com/cloud), for only 2.96 EUR/month you get a server in the cloud with 2 Gigs of RAM and 20GB of SSD and they seem really professional.

So sign up for a CX11 clud server.

Select the latest Debian image for it (10.3).

You should access it with ssh key.

If it is started connect to it via ssh ```ssh root@your.servers.ip.address```.

The only thing it needs is Java 8. On Debian 10 it Java 8 install looks like this :

```
sudo apt install apt-transport-https ca-certificates wget dirmngr gnupg software-properties-common
wget -qO - https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | sudo apt-key add -
sudo add-apt-repository --yes https://adoptopenjdk.jfrog.io/adoptopenjdk/deb/
sudo apt update
sudo apt install adoptopenjdk-8-hotspot
```

Check java with ```java -version```

Choool! Now let's create a release build!

First we have to tell leiningen to tell ring to use port 80 when in production so we add an uberjar part to profiles in project.clj.

```
(defproject hello-compojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 [com.datomic/datomic-pro "0.9.6024"]
                 [org.clojure/data.json "0.2.6"]
                 [ring-cors "0.1.13"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [ring/ring-defaults "0.3.2"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler hello-compojure.handler/app
         :nrepl {:start? true}}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}
   :uberjar {:dependencies [[javax.servlet/servlet-api "2.5"]
                            [ring/ring-mock "0.3.2"]]
             :ring {:port 80}}})
```

Then we have to tell the client side code to use the production server's ip address in production and use localhost only when in dev mode. Let's redefine ```server-url``` in src/cljs/hello_reagent/core.cljs.

```(def server-url (if js/goog.DEBUG "http://localhost:3000" "http://your.servers.ip.address"))```

We check if google closure lib's DEBUG flag is true or false to determine if we are a dev or a release build.

And now go to the root of ```hello-fullstack``` and type

```shadow-cljs release app```

Shadow creates a highly optimized js at ```/resources/public/js/compiled/main.js```
Delete ```cljs-runtime``` folder and ```manifest.edn``` from the same folder.

Now type

```lein ring uberjar``` at the root folder of ```hello-fullstack```.

lein packages the code under ```/target``` into two jars, we will use the standalone one.

Now we are ready to deploy stuff to the server!!!

First you have to copy datomic from your machine with everything, including dev.properties, to the server.

```rsync -v -r -e ssh datomic-pro-0.9.6024 root@116.203.87.141:/root/```

It will be copied to ```/root```.

Then copy the generated standalone jar from ```hello-fullstack/target``` folder to the server.

```rsync -v -r -e ssh hello-compojure-0.1.0-SNAPSHOT-standalone.jar root@your.servers.ip.address:/root/```

Then ssh to the server and start datomic first with reduced memory in case you are not on hetzner.

```bin/transactor -Xmx256m -Xms256m dev.properties```

Check if it starts up successfully. If yes, stop it with CTRL-C and start it in the background.

```nohup bin/transactor -Xmx256m -Xms256m dev.properties &```

Then start up the server with reduced memory usage.

```java -server -Xms256m -Xmx256m -Ddatomic.objectCacheMax=64m -Ddatomic.memoryIndexMax=64m -jar hello-compojure-0.1.0-SNAPSHOT-standalone.jar```

If it starts without problems, check your brand new site in the browser.

```http://your.servers.ip.address/```

It should show up. Now stop the server with CTRL+C and start it in the background.

```nohup java -server -Xms256m -Xmx256m -Ddatomic.objectCacheMax=64m -Ddatomic.memoryIndexMax=64m -jar hello-compojure-0.1.0-SNAPSHOT-standalone.jar &```

If you want to re-start them, kill them first with the ```killall java``` command.

And that's all!!! CONGRATULATIONS!!!

## Summary

So that's what full stack web development is about. You send and request data to/from a database through a server with api calls and resource requests and you maintain states in a single page web application and stylize it with css. Hope you liked the tutorial and you will stick with clojure! 

If you want to know how to send posts from the client side to the server and the database, how to store posts in markdown syntax and render them as html on the client side, how to create a riddle-based captchas, how to protect admin requests with password, how to animate components with reanimated, how to add menus, how to use ```a href```'s insted of ```on-click``` events for search engine optimization then check out [milgra.com github project](https://github.com/milgra/milgra.com)
