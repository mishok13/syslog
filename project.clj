(defproject me.mishok13/syslog "0.1.0-SNAPSHOT"
  :description "Implementation of RFC 5424 aka syslog protocol"
  :url "http://mishok13.me/libs/syslog"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]}})
