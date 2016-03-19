(ns learn-clojure.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(ns-name *ns*)
(ns-map *ns*)
(ns-interns *ns*)

(in-ns 'user)
(clojure.core/refer 'learn-clojure.core :only ['foo])
(foo "Hung")

(defmacro backwards [form]
  (reverse form))

(backwards (3 2 1 +))

(eval '(+ 1 2))

(eval '(def yolo 1))
yolo

(read-string "(+ 1 2)")
(read-string "(+ 1 2)")
(read-string "#(+ 1 %)")
(eval (read-string "(+ 1 2)"))

(defmacro my-print [expression]
  (do
    (println expression)
    expression))

(defmacro my-print-2
  [expression]
  (list 'let ['result expression]
        (list 'println 'result)
        'result))

(my-print (+ 1 2))

(defmacro my-when
  "Evaluates test. If logical true, evaluates body in an implicit do."
  {:added "1.0"}
  [test & body]
  `(if ~test
     (do ~@body)))

(my-when (= 1 1)
  (print 1)
  (print 2))

; future
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
