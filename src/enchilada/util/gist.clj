(ns enchilada.util.gist
  (:use [clj-time.format :only [parse]]
        [clj-time.coerce :only [to-long]])
  (:require [clj-http.client :as http]
            [clojure.data.json :as json]))

(defn- url [id] (str "https://api.github.com/gists/" id))

(def github-authentication
  (when-let [token (get (System/getenv) "GITHUB_OAUTH_TOKEN")]
    {"authorization" (str "token " token)}))

(defn fetch
  "Fetches a gist from the mothership and parses it from JSON into a keyword hash"
  [id]
  (let [{:keys [status headers body] :as resp} (http/get (url id) {:headers github-authentication})]
      (when (= status 200)
        (json/read-str body :key-fn keyword))))

(defn login-id
  "Constructs the github \"login/id\" path element"
  [gist]
  (str (get-in gist [:user :login]) "/" (:id gist)))

(defn last-modified
  "Converts the updated_at field into a number of milliseconds since 1.1.1970"
  [gist]
  (-> gist :updated_at parse to-long))
