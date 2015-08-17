(ns me.mishok13.syslog
  (:require [clj-time.core :as time.core]
            [clj-time.format :as time.format])
  (:import [java.io ByteArrayOutputStream]))

(def ^:private separator \space)
(def ^:private print-us-ascii (set (map char (range 33 127))))
(def ^:private nil-value \-)
(def ^:private bom (map char [0xEF 0xBB 0xBF]))
(def ^:private escape-characters [\" \\ \]])

(defn- printable-ascii?
  [bytes]
  (every? (fn [b] (<= 33 b 126)) (vec bytes)))

(defprotocol ISyslogFormattable
  (show [this] [this writer]
    "Format the part of syslog according to RFC 5424"))

(def ^:private timestamp-formatter
  (time.format/formatter "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))

(defn- write!
  [writer data & [encoding]]
  (cond
    (string? data) (write! writer (.getBytes data (or encoding "ASCII")))
    (instance? (Class/forName "[B") data) (.write writer data 0 (count data))
    (or (char? data) (integer? data) (instance? java.lang.Byte data)) (.write writer (int data))))

(defrecord Header [priority version timestamp hostname app-name proc-id msg-id]
  ISyslogFormattable
  (show [this writer]
    (doseq [data [(str "<" priority ">") (str version) separator
                  (time.format/unparse timestamp-formatter timestamp) separator
                  (str hostname) separator (str app-name) separator
                  (str proc-id) separator (str msg-id)]]
      (write! writer data))))

(defrecord StructuredData [elements]
  ISyslogFormattable
  (show [this writer]
    (cond
      (seq elements) writer
      :else (.write writer (int \-)))))

(extend-protocol ISyslogFormattable
  java.lang.String
  (show [this writer]
    (.write writer (- 239 128))
    (.write writer (- 187 128))
    (.write writer (- 191 129))
    (let [bytes (.getBytes this "UTF-8")]
      (.write writer bytes 0 (count bytes)))))

(defrecord SyslogMessage [header structured-data message])

(defn- valid-ascii?
  [s min-length max-length]
  (and (string? s)
       (let [bytes (.getBytes s "ASCII")]
         (and
          (printable-ascii? bytes)
          (<= min-length (count bytes) max-length)))))

(defn nilvalue
  [s]
  (cond
    (nil? s) "-"
    :else (str s)))

(defn make-message
  [& {:keys [facility severity version timestamp hostname app-name proc-id msg-id data message]}]
  (assert (and facility (<= 0 facility 23)))
  (assert (and severity (<= 0 severity 7)))
  (assert (and version (<= 0 version 99)))
  (assert (or (nil? hostname) (valid-ascii? hostname 1 255)))
  (assert (or (nil? app-name) (valid-ascii? app-name 1 48)))
  (assert (or (nil? proc-id) (valid-ascii? proc-id 1 128)))
  (let [priority (+ severity (* 8 facility))
        header (Header. (nilvalue priority)
                        (nilvalue version)
                        timestamp
                        (nilvalue hostname)
                        (nilvalue app-name)
                        (nilvalue proc-id)
                        (nilvalue msg-id))
        structured-data (StructuredData. data)
        message message]
    (SyslogMessage. header structured-data message)))

(defn format-message
  "Format message to a byte representation"
  [message & {:keys [no-bom?]}]
  ;; FIXME: make sure we can format message without BOM being present
  ;; in MSG part
  (let [writer (ByteArrayOutputStream.)]
    (show (:header message) writer)
    (.write writer (int separator))
    (show (:structured-data message) writer)
    (when (:message message)
      (.write writer (int separator))
      (show (:message message) writer))
    (.toByteArray writer)))
