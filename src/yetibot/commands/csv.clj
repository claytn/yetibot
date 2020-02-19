(ns yetibot.commands.csv
  (:require
   [taoensso.timbre :refer [debug info warn error]]
   [clojure.data.csv :as csv]
   [clojure.string :as string]
   [clj-http.client :as client]
   [yetibot.core.hooks :refer [cmd-hook]]))

(comment

  (def sample-csv
    "foo,bar,baz
A,1,x
B,2,y
C,3,z")

  (csv-cmd
   {:match
    ["https://docs.google.com/spreadsheets/d/1JIp3AjmPIA7T2aJsXPDaz7KxyxJLJGbgkR6r_dpvu_E/export?format=csv"]})

  (csv-data->maps (csv/read-csv sample-csv)))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn csv-cmd
  "csv <url> # parse CSV from <url>. Assumes first row columns are headers."
  [{[url] :match}]
  (info "csv" (pr-str url))
  (-> (client/get url)
      :body
      (clojure.string/replace  #"\uFEFF" "")
      csv/read-csv
      csv-data->maps))

(->
 "Summary,Description,Hours\r\nRebuild Y etibot dashboard in ClojureScript,\"Investigate Reagent + Refra me. ClojureScript gives us the benefit of access to the specs t hat describe the shape of Yetibot config, which would allow us to easily build a web-based config validator, as well as share any other future code between Yetibot and web.\",80\r\nAdd more examples to the docs,Show the power of Yetibot by composing mu ltiple commands in an expression pipeline,12\r\nDocument extend ing the GraphQL API,Can commands ad-hoc opt into providing gql resources? How do we compose them into a single gql spec at run time?,4\r\nPublish a blog post with the latest Yetibot updates, \"Web rewrite, history overhaul, Helm chart for Kubernetes depl oyments\",4\r\nFinish Mattermost adapter and write up a guide o n contributing adapters to Yetibot,\"And consider support for o ther adapters like Discord, Zulip, Spectrum and even Reddit\",1 2\r\nConsider a plugin based system where configuration drives which jars are pulled into Yetibot runtime,Lets us build a more minimal core Yetibot and allow consumers to pick and choose wh at functionality they want,60\r\nAllow commands to detect wheth er or not they are configured and offer helpful messages if use rs try to execute a non-configured command,Rely on the config s pecs to determine whether a command is configured. Maybe this b eomces a new parameter to cmd-hook ,16"
 csv/read-csv
 )

(defn csv-parse-cmd
  "csv parse <csv>"
  [{[_ text] :match raw :raw opts :opts}]
  (info "csv-parse"
        {:text text
         :raw raw
         :opts opts
         :joined (string/join \newline opts)
         })
  (-> (or (and (not (string/blank? text)) text)
          (string/join \newline opts))
      csv/read-csv
      csv-data->maps))

(cmd-hook #"csv"
          #"parse\s*(.*)" csv-parse-cmd
          #"(.+)" csv-cmd)
