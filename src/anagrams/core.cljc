(ns anagrams.core
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn- clean-word [word]
  (-> word
      (str/lower-case)
      (str/replace #"\W*" "")))

(defn- parse-word-list [word-list]
  (as-> word-list $
    (str/split-lines $)
    (map str/trim $)))

(defn- xform-word [word]
  (let [cleaned-word (clean-word word)]
    {(apply str (sort cleaned-word)) #{word}}))

(defn- xform-word-list [word-list]
  (->> word-list
       (parse-word-list)
       (remove #{""})
       (map xform-word)
       (apply merge-with set/union)))

(def ^{:private true} word-list (atom {}))

(defn set-word-list!
  ([words]
   {:pre [(not= "" words)]}
   (let [xformed-words-list (xform-word-list words)]
     (swap! word-list
            (fn [_old]
              xformed-words-list))
     "done")))

(defn append-word-list! [words]
  {:pre [(not= "" words)]}
  (let [xformed-words-list (xform-word-list words)]
    (swap! word-list
           (fn [old]
             (merge-with set/union old xformed-words-list)))
    "done"))

(defn anagrams-of
  "For a given string, returns exact anagrams. The word list should be set
  calling 'set-words-list!'. If you call set-words-list! with no arguments, it
  defaults 'words' found on a mac at /usr/share/dict/words. To use a different
  list of words, call the 'set-word-list!\" function with a newline delimited
  string of words"
  [string]
  (as-> string $
    (clean-word $)
    (sort $)
    (apply str $)
    (get @word-list $)))
