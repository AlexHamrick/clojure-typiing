(ns final.core
  (:gen-class)
  (:require [final.utils :as u])
  (:require [cljfx.api :as fx])
  (:require [final.utils :refer (->Entry)])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [final.http :as h]))

(declare
 generate-code
 map-from-file
 rend
 create-blank-map
 select-random-char
 generate-phrase
 new-phrase
 draw-and-update
 get-current-word
 text-input
 root
 char-range
 min-len
 rand-len
 gen-random-phrase
 max-attempts)

(defn -main [& args]
  (let [phrase "This is a demo phrase; following phrases are random."
        fx (fx/create-renderer)]
    (if (seq args)
      (let [arg-list (seq args)
            file (if (= "-code" (first arg-list))
                   (second arg-list)
                   (first arg-list))
            map (map-from-file file)
            code (h/get-code)]
        (if (= "-code" (first arg-list))
          (rend "" (generate-code map code) [(System/currentTimeMillis)] map file fx code)
          (rend "" (generate-phrase map) [(System/currentTimeMillis)] map file fx nil)))
        (rend "" phrase [(System/currentTimeMillis)] (create-blank-map) nil fx nil))))

(defn select-random-line
  [lines attempts char]
  (loop [num attempts
         line (rand-nth lines)]
    (if (str/includes? line char)
      line
      (if (= num 0)
        nil
        (recur (dec num) (rand-nth lines))))))

(defn select-code
  [lines chars avgs]
  (loop [char (select-random-char chars avgs)]
    (let [line (select-random-line lines (max-attempts) (str char))]
      (if (nil? line)
        (recur (select-random-char chars avgs))
        line)))
  )

(defn generate-code
  [char-map code]
  (let [vals (char-range)
        chars (map char vals)
        avgs (map u/get-avg (map char-map chars))]
    (select-code code chars avgs)))

(defn map-from-file
  [file]
  (if (and (not (nil? file))
       (.exists (io/as-file file)))
  (read-string (slurp file))
  (create-blank-map)))

(defn rend
  [current phrase times map profile fx code]
  (fx {:fx/type root
       :showing true
       :input current
       :phrase phrase
       :times times
       :map map
       :profile profile
       :fx fx
       :code code}))

(defn create-blank-map
  []
  (let [vals (char-range)
        chars (map char vals)
        entries (map u/default-entry chars)]
    (loop [library {}
           iter 0]
      (if (< iter (count vals))
        (recur (assoc library (nth chars iter) (nth entries iter)) (inc iter))
        library))))

(defn select-random-char
  [chars avgs]
  (let [total (reduce + avgs)
        random (rand)]
    (loop [iter 0
           remaining (* total random)]
      (let [val (- remaining (nth avgs iter))]
        (if (> val 0)
          (recur (inc iter) val)
          (nth chars iter))))))

(defn gen-random-phrase
  [chars avgs bot len]
  (reduce str (map char 
                   (take (+ (rand-int len) bot) 
                         (repeatedly #(select-random-char chars avgs)))))
  )

(defn generate-phrase
  [char-map]
  (let [vals (char-range)
        chars (map char vals)
        avgs (map u/get-avg (map char-map chars))]
    (gen-random-phrase chars avgs (min-len) (rand-len))))

(defn new-phrase
  [times phrase map profile fx code]
  (let [new-map (loop [iter 0
                       m map]
                  (if (not= (inc iter) (count times))
                    (let [time (- (get times (inc iter)) 
                                  (get times iter))
                          c (.charAt phrase iter)
                          entry (u/update-entry (get m c) time)]
                      (recur (inc iter) (u/set-entry m entry)))
                    m))]
    (when (not (nil? profile))
      (spit profile (prn-str new-map))
      (println ["saved profile" profile]))
    (if (not (nil? code))
      (rend "" (generate-code new-map code) 
            [(System/currentTimeMillis)] 
            new-map profile fx code)
      (rend "" (generate-phrase new-map) 
          [(System/currentTimeMillis)] 
          new-map profile fx code))))

(defn draw-and-update
  [new-val phrase times map profile fx code]
  (if (= new-val phrase)
    (new-phrase (conj times (System/currentTimeMillis)) 
                phrase map profile fx code)
    (let [len (count new-val)
          times-ct (count times)]
      (if (and (= times-ct len)
               (= (subs phrase 0 len) new-val))
        (rend new-val phrase (conj times (System/currentTimeMillis)) 
              map profile fx code)
        (rend new-val phrase times map profile fx code)))))

(defn get-current-word
  [phrase char-num]
  (let [spaces (keep-indexed #(when (= \space %2) %1) 
                             (into-array phrase))
        spaces (cons -1 spaces)
        spaces (conj (vec spaces) (count phrase))
        start (inc (last (filter #(< %1 char-num) spaces)))
        end (first (filter #(>= %1 char-num) spaces))]
    (subs phrase start end)))

(defn text-input [{:keys [current phrase times map profile fx code]}]
  {:fx/type :v-box
   :children [{:fx/type :label
               :text (get-current-word phrase 
                                       (dec (count times)))}
              {:fx/type :text-field
               :on-text-changed (fn [new-val] 
                                  (draw-and-update new-val phrase times map profile fx code))
               :text current}]})

(defn root [{:keys [showing input phrase times map profile fx code]}]
  {:fx/type :stage
   :width 1040
   :height 400
   :showing showing
   :scene {:fx/type :scene
           :root {:fx/type :v-box
                  :padding 50
                  :children [{:fx/type :v-box
                              :effect {:fx/type :drop-shadow}
                              :children [{:fx/type :label :text phrase}]}
                             {:fx/type text-input
                              :current input
                              :phrase phrase
                              :times times
                              :map map
                              :profile profile
                              :fx fx
                              :code code}]}}})

(defn char-range
  []
  (range 32 127))

(defn min-len
  []
  3)

(defn rand-len
  []
  (- 11 (min-len)))

(defn max-attempts
  []
  20)