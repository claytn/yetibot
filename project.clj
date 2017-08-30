(defproject yetibot "0.4.10"
  :description "A command line in your chat, where chat ∈ {irc,slack}."
  :url "https://github.com/yetibot/yetibot"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories [["releases" :clojars]]
  :profiles {:dev [:profiles/dev
                   {:source-paths ["dev"]
                    :exclusions [org.clojure/tools.trace]
                    :plugins [[lein-midje "3.2.1"]]
                    :dependencies [[org.clojure/tools.trace "0.7.9"]
                                   [midje "1.8.3"]]}]
             :uberjar {:uberjar-name "yetibot.jar"
                       :jvm-opts ["-server"]
                       :aot :all}
             :test {}}
  :repl-options {:init-ns yetibot.core.repl
                 :timeout 120000
                 :welcome (println "Welcome to the yetibot development REPL!")}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [yetibot.core "0.4.10"]

                 ; apis
                 [twitter-api "1.8.0"]
                 [clj-aws-s3 "0.3.10" :exclusions [joda-time]]

                 ; scraping
                 [org.jsoup/jsoup "1.10.3"]

                 ; utils
                 [org.flatland/useful "0.11.5"]
                 [json-path "1.0.0"]

                 ; polling
                 [robert/bruce "0.8.0"]

                 ; emojis
                 [com.vdurmont/emoji-java "3.3.0"]]
  :plugins [[lein-exec "0.3.5"]
            [lein-environ "1.0.3"]
            [lein-cloverage "1.0.7-SNAPSHOT"]
            [lein-ring "0.9.5"]
            [io.sarnowski/lein-docker "1.1.0"]]

  :aliases
  {"version" ["exec" "-ep" "(use 'yetibot.core.version)(print version)"]}
  ;; :pedantic :ignore

  :docker {:image-name "devth/yetibot"}

  :main yetibot.core.init)
