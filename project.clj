(defproject specalog "0.1.0"
  :description "Generate Datalog queries using Spec"
  :url "https://github.com/joshkh/specalog"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
