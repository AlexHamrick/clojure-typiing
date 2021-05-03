(ns final.http
  (:require [clj-http.client :as client])
  (:require [net.cgrand.enlive-html :as html])
  (:require [clojure.string :as str]))

(defn get-url
  [url]
  (str/split-lines (:body (client/get url))))
  ;; (:body (client/get "https://fungenerators.com/random/sentence")))
;; "https://randomwordgenerator.com/sentence.php"

(defn urls
  []
  ["https://randomwordgenerator.com/sentence.php" 
   "https://fungenerators.com/random/sentence"
   "https://randomword.com/sentence"
   "https://www.wordgenerator.net/random-sentence-generator.php"
   ])

(defn get-code
  []
  (map str/trim (remove str/blank? (flatten (map get-url (urls))))))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn headlines [url]
  (map html/text (html/select (fetch-url url) [:span.support-sentence])))

