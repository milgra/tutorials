# Clojure Kickstart By Examples

## Table Of Content

* Prerequisites
* Basics
* Data types
* Core functions
* Lexical bindings

## Prerequisites

Download [clojure](https://clojure.org/guides/getting_started) and [leiningen](https://leiningen.org/) to your platform and start a leiningen repl in a terminal by typing ```lein repl```. There are a lot of REPL's for clojure but leiningen's repl supports multi-line editing out of the box so we will go with this.
REPL stands for "read-eval-print-loop", an interactive environment where we can practice clojure. 
Press CTRL+D to cancel the actual expression, press ENTER to evaluate it, press CTRL+ENTER or SHIFT+ENTER to start a new line without evaluation.

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

```{:key0 "something" :key1 "anything"}``` or ```(hash-map :key0 "something" :key1 "anything")```

any word with a double colon is a keyword

let's get key 0 from this map

```({:key0 "something" :key1 "anything"} :key0)```

or

```(:key0 {:key0 "something" :key1 "anything"})```

## Core functions

define a name for the previous vector

```(def v1 [4 5 6])```

let's create a new vector based on v1 with all elements increased by one

```(map inc v1)```

so we mapped an existing vector to a new vector by applying inc(rease) function on every item

what if we want to increase elements by two?

```(map (fn [elem] (+ elem 2)) v1)```

so we created an anonymous custom function here for handling the elements

there's a syntactic sugar in clojure for shortening anonymous function definitions, let's try it

```(map #(+ % 2) v1)```

let's calculate the sum of the elements of the previous vector

```(reduce + v1)```

reduce creates one result after iterating through all elements ( reduces the elements ), map creates a sequence with the same item number as the original vector

define a name for the previous map

```(def m1 {:key0 "something" :key1 "anything"})```

let's create a new map based on a map replacing key1

```(assoc m1 :key1 "nothing")```

let's create a new map based on this map with "nnn" added to every value

```(into { } (map (fn [ elem ] { (key elem) (str (val elem) "nnn") } ) m1 ) )```

wooo, wait a minute, a lot of things happening here

we always have to start understanding from the innermost function

the anonymous function ```(fn [ elem ] { (key elem) (str (val elem) "nnn") } )``` gets one element at a time from the original map and that element is key-value pair

it returns a new, one-element length map ( because of the brackets ), with the original key and a modified value ( str concatenates two strings, the value of the element and ```nnn``` )

```map``` in front of it iterates through all elements of the original map and with the previous function it creates a sequence containing one element length maps

```into``` function at the beginning merges all one-length maps into one big map

and that's what clojure is about. you chain functions to get something totally different or slightly similar at the end that conforms your algorithm's needs

let's remember that we can simplify this expression with syntactic sugar :

```(into {} (map #(hash-map (key %) (str (val %) "nnn")) m1))```

or

```(into {} (map #(do { (key %) (str (val %) "nnn") }) m1))```

why do we need the ```do``` keyword here? because #% syntactic sugar encapsulates a single function call inside by default and ```do``` tells clojure that multiple expressions will follow where so we can now use the bracket syntax here for hash-map construction. You will use ```do``` in a lot of places, mainly in ```if``` and ```cond``` statements where syntax expects a single expression by default.

and let's implement the same functionality with reduce :

```(reduce (fn [res elem] (assoc res (key elem) (str (val elem) "nnn"))) {} m1)```

or in the simpler form

```(reduce #(assoc %1 (key %2) (str (val %2) "nnn")) {} m1)```

a reducer function always gets two parameters : the first is the starting value ( or the first item of the sequence of there is no starting value ) and the actual element

## Lexical bindings

Clojure has no variables, it has immutable values, still, we need a lot of named values for our functions/algorithms, we use the ```let``` from for this

```
(fn []
  (let [str1 "I am a string"
        num1 34
        num2 575.77
        vec1 [4 5 6]
        map1 {:a 3 :b "b"}]
    (println str1 num1 num2 vec1 map1)
  )
)
```

## Destructuring

Instead of extracting the needed values from maps and vectors in the function body line by line you can destruct these maps and vectors in place when receiving them.

```
(let [{ mykey :key1 }  {:key1 "something" :key2 "anything"}])

; mykey is "something" here

(let[ [_ myvalue] [200 300] ])

; myvalue is 300 here
```

## Threading

Often you will have to modify a value multiple times in a series. Instead of writing

```
(let [val1 (fun1 val0 arg1 arg2 argn)]
  (let [val2 (fun2 val1 arg1 arg2 argn)]
    (let [val3 (fun3 val2 arg1 arg2 argn)]
      val3)))
```

You can use the thread first and thread last macros :

```
(-> val1
  (fun1 arg1 arg2 argn)
  (fun2 arg1 arg2 argn)
  (fun3 arg1 arg2 argn))
```

## Conditional threading

What if you want to use threading, but you don't want to execure functions on the value in certain conditions?

```
(cond-> val1
  (something true) (fun1 arg1 arg2 argn)
  (something false) (fun2 arg1 arg2 argn)
  (something true) (fun3 arg1 arg2 argn))
```


## In action

```
(defn updatespeed [{[sx sy] :speed :as state}
                   {:keys [left right up down]}
                   time]
  (let [nsx (cond-> sx
              right (max (+ (* 0.4 time)) 10.0)
              left  (min (- (* 0.4 time)) -10.0)
              (not (and left right)) (* 0.9))
        dir (cond
              (and (> nnsx 0.0 ) right) 1
              (and (<= nnsx 0.0 ) left ) -1)]
    (-> state
        (assoc :speed [sx sy])
        (assoc :facing dir)))) ; TODO replace facing with dir
```
