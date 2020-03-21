# Clojure By Examples

## Table Of Content

* Prerequisites
* Basics
* Core functions

## Prerequisites

Download clojure to your platform and start it in a terminal by typing ```clojure```. It starts up the clojure REPL, the read-eval-print loop, an interactive environment where we can practice clojure.

## Basics

print something to the standard output

```(println 5)```

it prints 5 and nil. 5 printed to the console is the side effect of the function, nil is the result of the function, it returns nil because it is a side-effect only function. in clojure everything returns a value, functions will return with the result of the last expression of the function

try adding more parameters to the function :

```(println 4 6 "akarmi")```

note that function name is always the first word followed by an arbitrary number of arguments

now try simply entering println

```println```

clojure tells you what it knows about this symbol

let's ask for the short documentation of ```println```

```(doc println)```

## Data types

let's create a vector

```[4 5 6]``` or ```(vector 4 5 6)```

let's get the second element from this vector

```(nth [4 5 6] 2)```

or you can get the ```first``` , ```last``` , ```rest``` , ```next``` element(s)

create a list

```'(4 5 6)``` or ```(list 4 5 6)```

vector is an indexed array, list is a linked list. linked lists are faster but you cannot get arbitrary element from it, only ```first,rest,next``` works on them

let's create a map

```{:key0 "something" :key1 "anything"}```

words with double colons are keywords

let's get key 0 from this map

```({:key0 "something" :key1 "anything"} :key0)```

or

```(:key0 {:key0 "something" :key1 "anything"})```

## Core functions

let's create a new vector based on a vector with all elements increased by one

```(map inc [4 5 6])```

so we mapped an existing vector to a new vector by applying inc(rease) function on every item

what if we want to increase elements by two?

```(map (fn [elem] (+ elem 2)) [4 5 6])```

so we created an anonymous custom function here for handling the elements

there's a syntactic sugar in clojure for shortening anonymous function definitions, let's try it

```(map #(+ % 2) [4 5 6])```

let's calculate the sum of the elements of the previous vector

```(reduce + [4 5 6])```

reduce creates one result after iterating through all elements ( reduces the elements ), map creates a sequence with the same item number as the original vector

let's create a new map based on a map replacing key1

```(assoc {:key0 "something" :key1 "anything"} :key1 "nothing")```

let's create a new map based on this map with "nnn" added to every value

```
(into { } (map (fn [ elem ] { (key elem) (str (val elem) "nnn") } ) {:key0 "something" :key1 "anything" } ) )
```

wooo, wait a minute, a lot of things happening here

we always have to start understanding from the innermost function

the anonymous function ```fn [elem]``` gets a key-value pair from the original map

it creates a one-element length map ( because of the brackets ), with the original key and a modified value ( str concatenates two strings, the value of the element and ```nnn``` )

map iterates through all elements of the original map and creates a sequence containing one element length maps

and ```into``` function at the beginning merges all one-length maps in the vector created by map into one big map

and that's what clojure is about. you chain data processing functions and anonymus functions to get something totally different or totally similar at the end that conforms your algorithm's needs

let's remember that we can simplify this expression with syntactic sugar :

```
(into {} (map #(hash-map (key %) (str (val %) "nnn")) {:key0 "something" :key1 "anything"}))  
```

and let's implement the same functionality with reduce :

```
(reduce (fn [res elem] (assoc res (key elem) (str (val elem) "nnn"))) {} {:key0 "something" :key1 "anything"}) 
```

or in the simpler form

```
(reduce #(assoc %1 (key %2) (str (val %2) "nnn")) {} {:key0 "something" :key1 "anything"})  
```

## Enter the editor
