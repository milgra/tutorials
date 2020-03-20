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
* Deploying to a server
* Setting up datomic


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

# Generating html on the server

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
  [:div {:key "page"}
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






so we now have two webservers running on our machine, one for ring/compojure from the previous examples and one for shadow-cljs development/evaluation, we also have two nrepl ports, one for ring/compojure development and one for the client-side development and we have two separate projects! let's merge at least the project to simplify things.

## Deploying to a server

## Summary

so that's what full stack web development is about. you add and request data to/from a database through a server with api calls and resource requests, and you maintain states in a single page web application and stylize it with css. hope you liked the tutorial and you will stick with clojure! 














create pager, update css

create menus for all page, request data from server, mock server responses

create database connection, show how to test server inline

use data returned from server on the client side

use a href's for url browsability & search engine optimization

deploy the whole stuff to hetzner

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
