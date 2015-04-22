(ns me.mishok13.syslog)

(def ^:private separator \space)
(def ^:private print-us-ascii (set (map char (range 33 127))))
(def ^:private nil-value \-)
(def ^:private bom (map char [0xEF 0xBB 0xBF]))
(def ^:private escape-characters [\" \\ \]])

(defrecord Header [priority version timestamp hostname app-name proc-id msg-id])
(defrecord SyslogMessage [header structured-data message])
