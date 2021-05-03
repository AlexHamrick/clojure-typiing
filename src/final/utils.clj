(ns final.utils)

(defrecord Entry [character count time])

(defn default-entry
  [c]
  (->Entry c 1 1000))

(defn get-char
  [entry]
  (:character entry))

(defn get-count
  [entry]
  (:count entry))

(defn add-to-count
  [entry to-add]
  (assoc entry :count (+ to-add (get-count entry))))

(defn get-time
  [entry]
  (:time entry))

(defn add-to-time
  [entry to-add]
  (assoc entry :time (+ to-add (get-time entry))))

(defn get-avg
  [entry]
  (/ (get-time entry) (get-count entry)))

(defn set-entry
  [m entry]
  (assoc m (get-char entry) entry))

(defn update-entry
  [entry time-to-add]
  (add-to-time (add-to-count entry 1) time-to-add))

