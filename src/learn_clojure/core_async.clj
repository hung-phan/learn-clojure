(ns learn-clojure.core-async
  (:require [clojure.core.async
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))

(def echo-chan (chan))

(go
  (println (<! echo-chan)))

(>!! echo-chan "ketchup")

;; alts
(defn upload
  [headshot c]
  (go (Thread/sleep (rand 100))
      (>! c headshot)))

(let [c1 (chan)]
  (upload "serious.jpg" c1)
  (let [[headshot _] (alts!! [c1 (timeout 20)])]
    (if headshot
      (println "Sending headshot notification for" headshot)
      (println "Timed out!"))))

;; pipeline
(defn upper-caser
  [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/upper-case (<! in)))))
    out))

(defn reverser
  [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/reverse (<! in)))))
    out))

(defn printer
  [in]
  (go (while true (println (<! in)))))

(def in-chan (chan))

(def upper-caser-out (upper-caser in-chan))

(def reverser-out (reverser upper-caser-out))

(printer reverser-out)

(>!! in-chan "redrum")

(>!! in-chan "repaid")
