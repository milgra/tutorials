# Full stack web develoment with clojure and datomic

## Prerequisites

You should be familiar with [clojure](clojure-by-examples.md) and you should have a code editor with a clojure plugin for inline evaluation.

## Preface

Simply put, full stack web development is generating web pages for the user's browser based on the user's requests. This can happen in the browser on the client side and on the web server machine on the server side, usually both side is involved.

A web server is a program that listens for connections from browsers and based on the route and the parameters of the request it passes back data. This data can be a web page written in HTML or any kind of data that the client side code will insert into the existing client side HTML page.

Example URL ( Uniform Resource Locator ) :

https://example.com/user/1134?showage=false&showhair=false

the server address is : [https://example.com] 

the route is : [/user/1134]

the parameters are : [showage=false&showhair=false]

## Serving a static html page

Create a compojure project template with leiningen :

```lein new compojure hello-compojure```

serve static resource with compojure

## Generating a html page on the server side

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
