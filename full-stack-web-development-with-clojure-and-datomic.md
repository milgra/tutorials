# Full stack web develoment with clojure and datomic

## Prerequisites

You should be familiar with [clojure](clojure-by-examples.md) and you should have a code editor with a clojure plugin for inline evaluation. You should be familiar with command line execution and terminals, you will need at least three terminal windows or four if you use a terminal-based editor.

## Preface

Simply put, full stack web development is generating web pages for the user's browser based on the user's requests. This can happen in the browser on the client side and on the web server machine on the server side, usually both side is involved.

A web server is a program that listens for connections from browsers and based on the route and the parameters of the request it passes back data. This data can be a web page written in HTML or any kind of data that the client side code will insert into the existing client side HTML page.

Example URL ( Uniform Resource Locator ) :

https://example.com/user/1134?showage=false&showhair=false

the server address is : [https://example.com] 

the route is : [/user/1134]

the parameters are : [showage=false&showhair=false]

## Setting up the server

Create a compojure project template with leiningen :

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
houl
Connect your editor's nrepl plugin to this port. In emacs you should open the minibuffer with ALT+X, enter ```cider-connect-clj``` for host type ```localhost``` , for port type the resulting port from the previous command.

To check if inline eval is working open ```src/hello_compojure/coresh .clj``` in your editor and insert this somewhere :

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

the simplest task a web server can do is server a static html page. let's try this

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

serve static resource with compojure

# Generating a html page on the server side

create and server dynamic page on server side

# Generating html on the client side

create dynamic page on client side and serve it as resource

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
