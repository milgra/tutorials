Download clojure on your platform and start it in a terminal by typing ```clojure```. It starts up the clojure REPL, the read-eval-print loop, an interactive environment where we can practice clojure.

# Part I : core functions

print something to the standard output

```(println 5)```

it prints 5 and nil. 5 printed to the console is the side effect of the function, nil is the result of the function, it doesn't returns anything because it is a side-effect only function

try adding more parameters to the function :

```(println 4 6 "akarmi")```

now try simply entering println

```println```

clojure tells you what it knows about this symbol

now add two numbers

```(+ 3 5)```

note that the function call is always the first element followed by arbitrary numbers of arguments

let's create a vector

```[4 5 6]```

it will return the same vector

let's create a new vector based on this vector with all elements increased by one

```(map inc [4 5 6])```

so we mapped an existing vector to a new vector by applying inc(rease) function on every item

what if we want to increase elements by two?

```(map (fn inctwo [elem] (+ elem 2)) [4 5 6])```

so we created a custom function here for handling the elements

there's a syntactic sugar in clojure for shortening function definitions, let's try it

```(map #(+ % 2) [4 5 6])```

let's create a map

```{:key0 "something" :key1 "anything"}```

words with double colons are keywords

let's print key 0 from this map

```({:key0 "something" :key1 "anything"} :key0)```

or

```(:key0 {:key0 "something" :key1 "anything"})```

let's create a new map based on this map replacing key1

```(assoc {:key0 "something" :key1 "anything"} :key1 "nothing")```

let's create a new map based on this map with "nnn" added to every value

```(map (fn mod [ elem ] {(key elem) (str (val elem) "nnn")}) {:key0 "something" :key1 "anything"})```
