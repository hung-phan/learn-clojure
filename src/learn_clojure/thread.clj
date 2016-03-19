(ns learn-clojure.thread
  (:require [clj-http.client :as client]
            [clojure.pprint :refer [pprint]]))

(future (Thread/sleep 2000)
        (println "I'll print after 2 seconds"))

(let [result (future
               (Thread/sleep 2000)
               (+ 1 2))]
  (println "I'll print immediately")
  (prn @result))

(realized? (future (Thread/sleep 1000)))

(def jackson-5-delay
  (delay (let [message "Just call my name and I'll be there"]
           (println "First deref:" message)
           message)))

(force jackson-5-delay)
@jackson-5-delay

(def my-promise (promise))
(deliver my-promise (+ 1 3))

@my-promise

(defmacro enqueue
  ([q concurrent-promise-name concurrent serialized]
   `(let [~concurrent-promise-name (promise)]
      (future (deliver ~concurrent-promise-name ~concurrent))
      (deref ~q)
      ~serialized
      ~concurrent-promise-name))
  ([concurrent-promise-name concurrent serialized]
   `(enqueue (future) ~concurrent-promise-name ~concurrent ~serialized)))

(defmacro wait
  "Sleep `timeout` seconds before evaluating body"
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))

(-> (enqueue saying (wait 200 "'Ello, gov'na!") (println @saying))
    (enqueue saying (wait 400 "Pip pip!") (println @saying))
    (enqueue saying (wait 100 "Cheerio!") (println @saying)))

(time @(-> (enqueue saying (wait 200 "'Ello, gov'na!") (println @saying))
           (enqueue saying (wait 400 "Pip pip!") (println @saying))
           (enqueue saying (wait 100 "Cheerio!") (println @saying))))

(defn google-search [query]
  (client/get (str "https://google.com/search?q=" query)))

(def result (:body (google-search "hung phan")))
result

(spit "result.html" result)
