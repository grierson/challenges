(ns diamond.core-test
  (:require [clojure.test :refer [deftest testing is are]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [clojure.string :as str]
            [diamond.core :as sut]
            [diamond.spec.core-spec :as sut-spec]
            [clojure.spec.alpha :as s]))

;; Example
(deftest diamond
  (testing "In alphabetical order"
    (are [expected actual] (= expected actual)
      "A" (sut/make \A)
      " A \nB B\n A " (sut/make \B)
      "  A  \n B B \nC   C\n B B \n  A  " (sut/make \C))))

;; Helpers
(defn spaces [line]
  (take-while #(= % \space) line))

(defn alphabet [start end]
  (map char (range (int start) (inc (int end)))))

(defn count-spaces
  [rows letter]
  (->> rows
       (filter #(not (str/includes? % "A")))
       (drop-while #(not (str/includes? % (str letter))))
       (map (comp count spaces))
       (map-indexed vector)))

(defn is-two-identical-letters?
  [line]
  (let [has-identical-letters (= 1 (-> line distinct count))
        has-two-letters (= 2 (count line))]
    (and has-identical-letters has-two-letters)))

(defn top-rows
  [rows letter]
  (take-while #(not (str/includes? % (str letter))) rows))

(defn bottom-rows
  [rows letter]
  (->> rows
       (drop-while #(not (str/includes? % (str letter))))
       (drop 1)
       (reverse)))

;; Property
(defspec first-row-contains-a
  (prop/for-all [letter (s/gen ::sut-spec/letter)]
                (= (-> (sut/make letter) str/split-lines first str/trim)
                   "A")))

(defspec last-row-contains-a
  (prop/for-all [letter (s/gen ::sut-spec/letter)]
                (= (-> (sut/make letter) str/split-lines last str/trim)
                   "A")))

(defspec symmetric-contour
  (prop/for-all [letter (s/gen ::sut-spec/letter)]
                (let [diamond (sut/make letter)
                      rows (str/split-lines diamond)]
                  (every? true? (map #(= (spaces %) (spaces (reverse %))) rows)))))

(defspec alphabetical-order
  (prop/for-all [letter (s/gen ::sut-spec/letter)]
                (let [diamond (sut/make letter)
                      letters (alphabet \A letter)
                      expected (concat letters (-> letters reverse rest))
                      rows (->> diamond
                                str/split-lines
                                (map str/trim)
                                (map first))]
                  (= rows expected))))

(defspec equal-height-width
  (prop/for-all [letter (s/gen ::sut-spec/letter)]
                (let [diamond (sut/make letter)
                      rows (str/split-lines diamond)
                      expected (count rows)]
                  (every? true? (map #(= (count %) expected) rows)))))

(defspec inner-rows-have-identical-letters
  (prop/for-all [letter (s/gen ::sut-spec/letter)]
                (let [diamond (sut/make letter)
                      rows (-> diamond str/split-lines)]
                  (->> rows
                       (filter #(not (str/includes? % "A")))
                       (map str/trim)
                       (map is-two-identical-letters?)))))

(defspec lower-left-triangle
  (prop/for-all [letter (s/gen ::sut-spec/letter)]
                (let [diamond (sut/make letter)]
                  (if (= \A letter)
                    true
                    (let [rows (str/split-lines diamond)
                          spacing (count-spaces rows letter)]
                      (every? true? (map (fn [[line spaces]] (= line spaces)) spacing)))))))

(defspec diamond-symmetric-horizontal
  (prop/for-all [letter (s/gen ::sut-spec/letter)]
                (let [diamond (sut/make letter)
                      rows (str/split-lines diamond)]
                  (= (top-rows rows letter)
                     (bottom-rows rows letter)))))