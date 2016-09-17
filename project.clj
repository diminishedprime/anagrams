(defproject anagrams "2"
  :description "Anagrams is a library designed to give all anagrams of a word,
  provided a dictionary of words is provided."
  :url "https://github.com/diminishedprime/anagrams"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:test {:dependencies [[org.clojure/test.check "0.9.0"]]}}
  :dependencies [[org.clojure/clojure "1.8.0"]])
