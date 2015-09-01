(ns me.mishok13.syslog-test
  (:require [me.mishok13.syslog :refer :all]
            [midje.sweet :refer :all]
            [clj-time.core :refer [date-time]])
  (:import [org.joda.time DateTime DateTimeZone]))

;; The following test is using examples from RFC 5424, section 6.5;
;; see more here https://tools.ietf.org/html/rfc5424#section-6.5
;; (fact
;;  "Syslog message is formatted correctly"
;;  ;;
;;  (vec (format-message (make-message )))
;;  =>
;;  ;; This is generated from the following message:
;;  ;;
;;  ;; where BOM was replaced with an actual BOM

;;  ;; (format-message (make-message :version 1)) =>
;;  ;; "<165>1 2003-08-24T05:14:15.000003-07:00 192.0.2.1 myproc 8710 - - %% It's time to make the do-nuts."
;;  )


(tabular
 (fact
  (vec (format-message (apply make-message ?args))) => ?bytes)
 ?args ?bytes
 [:version 1 :facility 4 :severity 2 :timestamp (date-time 2003 10 11 22 14 15 3)
  :hostname "mymachine.example.com" :app-name "su" :proc-id nil :msg-id "ID47"
  :message "'su root' failed for lonvick on /dev/pts/8"]
 ;; Actual message:
 ;; "<34>1 2003-10-11T22:14:15.003Z mymachine.example.com su - ID47 - BOM'su root' failed for lonvick on /dev/pts/8"
 [60 51 52 62 49 32 50 48 48 51 45 49 48 45 49 49 84 50 50 58 49 52 58 49 53 46
  48 48 51 90 32 109 121 109 97 99 104 105 110 101 46 101 120 97 109 112 108 101
  46 99 111 109 32 115 117 32 45 32 73 68 52 55 32 45 32 -17 -69 -65 39 115 117
  32 114 111 111 116 39 32 102 97 105 108 101 100 32 102 111 114 32 108 111 110
  118 105 99 107 32 111 110 32 47 100 101 118 47 112 116 115 47 56]
 ;; Note that this uses a modified version of the test, since this
 ;; library does not support microsecond precision. The original
 ;; example message in RFC is:
 ;; "<165>1 2003-08-24T05:14:15.000003-07:00 192.0.2.1 myproc 8710 - - %% It's time to make the do-nuts."
 ;; Message as used for testing is:
 ;; "<165>1 2003-08-24T05:14:15.003-07:00 192.0.2.1 myproc 8710 - - %% It's time to make the do-nuts."
 ;; Also note that this version explicitly drops BOM before MSG part
 [:version 1 :facility 20 :severity 5
  :timestamp (DateTime. 2003 8 24 5 14 15 3 (DateTimeZone/forOffsetHours -7))
  :force-utc? false
  :hostname "192.0.2.1" :app-name "myproc" :proc-id "8710"
  :message (.getBytes "%% It's time to make the do-nuts." "ASCII") :bom? false]
 [60 49 54 53 62 49 32 50 48 48 51 45 48 56 45 50 52 84 48 53 58 49 52 58 49 53 46 48 48 51 45 48 55 58 48 48 32 49 57 50 46 48 46 50 46 49 32 109 121 112 114 111 99 32 56 55 49 48 32 45 32 45 32 37 37 32 73 116 39 115 32 116 105 109 101 32 116 111 32 109 97 107 101 32 116 104 101 32 100 111 45 110 117 116 115 46])

(tabular
 (fact
  (vec (show (make-structured-data ?elements))) => ?bytes)
 ?elements ?bytes
 ;; Empty message produces "-" as a result
 [] [45]
 [{:id "exampleSDID@32473" :params [["iut" "3"] ["eventSource" "Application"] ["eventID" "1011"]]}] [91 101 120 97 109 112 108 101 83 68 73 68 64 51 50 52 55 51 32 105 117 116 61 34 51 34 32 101 118 101 110 116 83 111 117 114 99 101 61 34 65 112 112 108 105 99 97 116 105 111 110 34 32 101 118 101 110 116 73 68 61 34 49 48 49 49 34 93])
