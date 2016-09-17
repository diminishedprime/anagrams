(ns anagrams.core-test
  (:require [clojure.test :as t]
            [anagrams.core :as sut]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :as tcct]))

(defn reset-list! []
  (sut/set-word-list! "peter"))


(t/deftest clean-word-test

  (t/testing "Testing some simple cases of words that should be cleaned"
    (let [dirty-word "O'Reily"
          actual (#'sut/clean-word dirty-word)]
      (t/is (= actual "oreily"))))

  (t/testing "Testing with a clean word"
    (let [clean-word "hello"
          actual (#'sut/clean-word clean-word)]
      (t/is (= actual "hello")))))

(t/deftest parse-word-list-test

  (t/testing "Testing with a happy path"
    (let [newline-delimeted-list-of-words "hello\nthere\njohnny"
          actual (#'sut/parse-word-list newline-delimeted-list-of-words)]
      (t/is (= actual ["hello" "there" "johnny"]))))

  (t/testing "Testing with some words that need cleanup"
    (let [newline-delimeted-list-of-words "  hello \n       there\njohnny"
          actual (#'sut/parse-word-list newline-delimeted-list-of-words)]
      (t/is (= actual ["hello" "there" "johnny"])))))

(t/deftest xform-word-test

  (t/testing "Testing that xform-word returns a map of the word sorted to the
  exact word in a list"
    (let [my-word "#hastag"
          actual (#'sut/xform-word my-word)]
      (t/is (= actual '{"aaghst" #{"#hastag"}})))))

(t/deftest xform-word-list-test

  (t/testing "Testing a simple case of transforming the newline delimeted string
  to the correct representation."
    (let [newline-delimeted-list-of-words "  hello \n       there\njohnny"
          actual (#'sut/xform-word-list newline-delimeted-list-of-words)]
      (t/is (= actual {"ehllo" #{"hello"}
                       "eehrt" #{"there"}
                       "hjnnoy" #{"johnny"}})))))


(t/deftest set-word-list!-test

  (t/testing "Testing that set-word-list also tranforms input correctly"
    (let [_ (reset-list!)
          newline-delimeted-list-of-words "  hello \n       there\njohnny"
          actual (#'sut/set-word-list! newline-delimeted-list-of-words)
          actual-result @@#'sut/word-list]
      (t/is (= actual-result {"ehllo" #{"hello"}
                              "eehrt" #{"there"}
                              "hjnnoy" #{"johnny"}}))))

  (t/testing "Testing that set-word-list throws assertion error when passed an
  empty string"
    (try
      (let [_ (reset-list!)
            empty-string ""
            actual (#'sut/set-word-list! empty-string)]
        (t/is false))
      (catch AssertionError e
        (t/is true)))))

(t/deftest append-word-list!
  (t/testing "Testing that append-word-list also tranforms input correctly"
    (let [_ (reset-list!)
          newline-delimeted-list-of-words "  hello \n       there\njohnny"
          actual (#'sut/append-word-list! newline-delimeted-list-of-words)
          actual-result @@#'sut/word-list]
      (t/is (= actual-result '{"eeprt" #{"peter"}
                               "ehllo" #{"hello"}
                               "eehrt" #{"there"}
                               "hjnnoy" #{"johnny"}}))))

  (t/testing "Testing that append-word-list won't double add a word"
    (let [_ (reset-list!)
          newline-delimeted-list-of-words "  hello \n       there\njohnny"
          invoke-1 (#'sut/append-word-list! newline-delimeted-list-of-words)
          invoke-2 (#'sut/append-word-list! newline-delimeted-list-of-words)
          actual-result @@#'sut/word-list]
      (t/is (= actual-result '{"eeprt" #{"peter"}
                               "ehllo" #{"hello"}
                               "eehrt" #{"there"}
                               "hjnnoy" #{"johnny"}}))))

  (t/testing "Testing that append-word-list throws assertion error when passed an
  empty string"
    (try
      (let [_ (reset-list!)
            empty-string ""
            actual (#'sut/append-word-list! empty-string)]
        (t/is false))
      (catch AssertionError e
        (t/is true)))))

(t/deftest anagrams-of-test

  (t/testing "Testing on a large anagram list with one non-anagram word"
    (let [word-list "angor\nargon\ngoran\ngrano\ngroan\nnagor\nOrang\norang\norgan\nrogan\nRonga\nother"
          _! (sut/set-word-list! word-list)
          actual (sut/anagrams-of "groan")]
      (t/is (= actual #{"Orang"
                        "goran"
                        "angor"
                        "groan"
                        "orang"
                        "organ"
                        "argon"
                        "nagor"
                        "grano"
                        "rogan"
                        "Ronga"})))))

;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Property Based Tests ;;
(tcct/defspec clean-word-always-returns-a-string
  (prop/for-all
    [my-str gen/string]
    (let [actual (#'sut/clean-word my-str)]
      (string? actual))))

(tcct/defspec clean-word-returns-only-a-Z
  (prop/for-all
    [my-str gen/string]
    (let [actual (#'sut/clean-word my-str)]
      (re-matches #"^\w*" actual))))

(tcct/defspec clean-word-returns-a-string-of-equal-or-lesser-length
  (prop/for-all
    [my-str gen/string]
    (let [actual (#'sut/clean-word my-str)]
      (<= (count actual) (count my-str)))))
