(ns advent2018.001
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn foo [state expr]
  (let [op (-> expr first str symbol resolve)
        v (->> expr rest (apply str) read-string)]
    (op state v)))

(def data (-> "001.txt"
              io/resource
              slurp
              string/split-lines))

(reduce foo 0 data)