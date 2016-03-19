(ns learn-clojure.macro)

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
