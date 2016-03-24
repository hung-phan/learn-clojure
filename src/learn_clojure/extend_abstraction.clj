(ns learn-clojure.extend-abstraction)

;; multi method
(defmulti full-moon-behavior
          (fn [were-creature] (:were-type were-creature)))

(defmethod full-moon-behavior :wolf
  [were-creature]
  (str (:name were-creature) " will howl and murder"))

(defmethod full-moon-behavior :simmons
  [were-creature]
  (str (:name were-creature) " will encourage people and sweat to the oldies"))

(defmethod full-moon-behavior :default
  [were-creature]
  (str (:name were-creature) " will stay up all night fantasy footballing"))

(full-moon-behavior {:were-type :wolf
                     :name      "Rachel from next door"})

(full-moon-behavior {:were-type :simmons
                     :name      "Andy the baker"})

(full-moon-behavior {:were-type :office-worker
                     :name      "Jimmy from sales"})

(defprotocol Psychodynamics
  "Plumb the inner depths of your data types"
  (thoughts [x] "The data type's innermost thoughts")
  (feelings-about [x] [x y] "Feelings about self or other"))

(extend-type java.lang.String
  Psychodynamics
  (thoughts [x] (str x " thinks, 'Truly, the character defines the data type'"))
  (feelings-about
    ([x] (str x " is longing for a simpler way of life"))
    ([x y] (str x " is envious of " y "'s simpler way of life"))))

(thoughts "blorb")
(feelings-about "schmorb")
(feelings-about "schmorb" 2)

;; What if you want to provide a default implementation, like you did with multimethods?
;; To do that, you can extend java.lang.Object. This works because every type in Java (and hence, Clojure) is
;; a descendant of java.lang.Object. If that doesn’t quite make sense (perhaps because you’re not familiar with
;; object-oriented programming), don’t worry about it—just know that it works. Here’s how you would use this
;; technique to provide a default implementation for the Psychodynamics protocol:

(extend-type java.lang.Object
  Psychodynamics
  (thoughts [x] "Maybe the Internet is just a vector for toxoplasmosis")
  (feelings-about
    ([x] "meh")
    ([x y] (str "meh about " y))))

(thoughts 3)

(feelings-about 3)

(feelings-about 3 "blorb")

;; Instead of making multiple calls to extend-type to extend multiple types, you can use extend-protocol,
;; which lets you define protocol implementations for multiple types at once. Here’s how you’d define the preceding
;; protocol implementations:

(extend-protocol Psychodynamics
  java.lang.String
  (thoughts [x] "Truly, the character defines the data type")
  (feelings-about
    ([x] "longing for a simpler way of life")
    ([x y] (str "envious of " y "'s simpler way of life")))

  java.lang.Object
  (thoughts [x] "Maybe the Internet is just a vector for toxoplasmosis")
  (feelings-about
    ([x] "meh")
    ([x y] (str "meh about " y))))

(defprotocol WereCreature
  (roar [x]))

(defrecord WereWolf [name title]
  WereCreature
  (roar [_]
    (str name " will howl and murder")))

(WereWolf. "David" "London Tourist")

(def jacob (->WereWolf "Jacob" "Lead Shirt Discarder"))
(.name jacob)
(:name jacob)
(get jacob :name)

(roar jacob)
