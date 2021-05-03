(defproject final "0.1.0-SNAPSHOT"
  :description "A project for practicing typing that is customized to your individual character typing speeds"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojure-contrib "1.1.0"]
                 [jline "0.9.94"]
                 [cljfx "1.7.13"]
                 [clj-http "3.12.0"] 
                 [enlive "1.1.1"]
                 ]
  :repl-options {:init-ns final.core}
  :main final.core)
