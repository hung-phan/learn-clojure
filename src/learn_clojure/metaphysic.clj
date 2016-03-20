(ns learn-clojure.metaphysic
  (:require [clojure.pprint :refer [pprint]]))

;; atom
(def fred (atom {:cuddle-hunger-level 0
                 :percent-deteriorated 0}))

(defn shuffle-speed
  [zombie]
  (* (:cuddle-hunger-level zombie)
     (- 100 (:percent-deteriorated zombie))))

(defn shuffle-alert
  [key watched old-state new-state]
  (let [sph (shuffle-speed new-state)]
    (if (> sph 5000)
      (do
        (println "Run, you fool!")
        (println "The zombie's SPH is now " sph)
        (println "This message brought to your courtesy of " key))
      (do
        (println "All's well with " key)
        (println "Cuddle hunger: " (:cuddle-hunger-level new-state))
        (println "Percent deteriorated: " (:percent-deteriorated new-state))
        (println "SPH: " sph)))))

(reset! fred {:cuddle-hunger-level 22
              :percent-deteriorated 2})

(add-watch fred :fred-shuffle-alert shuffle-alert)

(swap! fred update-in [:percent-deteriorated] + 1)
(swap! fred update-in [:cuddle-hunger-level] + 30)

(defn percent-deteriorated-validator
  [{:keys [percent-deteriorated]}]
  (and (>= percent-deteriorated 0)
       (<= percent-deteriorated 100)))

(def bobby
  (atom
   {:cuddle-hunger-level 0 :percent-deteriorated 0}
   :validator percent-deteriorated-validator))

(swap! bobby update-in [:percent-deteriorated] + 200)

(defn percent-deteriorated-validator
  [{:keys [percent-deteriorated]}]
  (or (and (>= percent-deteriorated 0)
           (<= percent-deteriorated 100))
      (throw (IllegalStateException. "That's not mathy!"))))

(def bobby
  (atom
   {:cuddle-hunger-level 0 :percent-deteriorated 0}
   :validator percent-deteriorated-validator))

(swap! bobby update-in [:percent-deteriorated] + 200)

;; ref
(def sock-varieties
  #{"darned" "argyle" "wool" "horsehair" "mulleted"
    "passive-aggressive" "striped" "polka-dotted"
    "athletic" "business" "power" "invisible" "gollumed"})

(defn sock-count
  [sock-variety count]
  {:variety sock-variety
   :count count})

(defn generate-sock-gnome
  "Create an initial sock gnome state with no socks"
  [name]
  {:name name
   :socks #{}})

(def sock-gnome (ref (generate-sock-gnome "Barumpharumph")))
(def dryer (ref {:name "LG 1337"
                 :socks (set (map #(sock-count % 2) sock-varieties))}))

(pprint
 (:socks @dryer))

(defn steal-sock
  [gnome dryer]
  (dosync
   (when-let [pair (some #(if (= (:count %) 2) %) (:socks @dryer))]
     (let [updated-count (sock-count (:variety pair) 1)]
       (alter gnome update-in [:socks] conj updated-count)
       (alter dryer update-in [:socks] disj pair)
       (alter dryer update-in [:socks] conj updated-count)))))
(steal-sock sock-gnome dryer)

(pprint
 (:socks @dryer))

(defn similar-socks
  [target-sock sock-set]
  (filter #(= (:variety %) (:variety target-sock)) sock-set))

(similar-socks (first (:socks @sock-gnome)) (:socks @dryer))

(pprint
 (:socks @dryer))

(def counter (ref 0))

(future
  (dosync
   (alter counter inc)
   (println @counter)
   (Thread/sleep 500)
   (alter counter inc)
   (println @counter)))

(Thread/sleep 250)

(println @counter)

;; commute allows you to update a ref’s state within a transaction, just like alter.
;; However, its behavior at commit time is completely different. Here’s how alter behaves:

;; Reach outside the transaction and read the ref’s current state.
;; Compare the current state to the state the ref started with within the transaction.
;; If the two differ, make the transaction retry.
;; Otherwise, commit the altered ref state.
;; commute, on the other hand, behaves like this at commit time:

;; Reach outside the transaction and read the ref’s current state.
;; Run the commute function again using the current state.
;; Commit the result.

(defn sleep-print-update
  [sleep-time thread-name update-fn]
  (fn [state]
    (Thread/sleep sleep-time)
    (println (str thread-name ": " state))
    (update-fn state)))

(def counter (ref 0))

(future (dosync (commute counter (sleep-print-update 100 "Thread A" inc))))
(future (dosync (commute counter (sleep-print-update 150 "Thread B" inc))))
