(ns app.db
  (:require [next.jdbc :as jdbc]
            [honey.sql :as sql]))

(defn format
  "Used for formatting HoneySQL's data format to a PostgreSQL string.
  Formats snake-case to kebab_case in keywords."
  [data]
  (sql/format data {:quoted-snake true}))

(defn execute!
  "Call next.jdbc's `execute!`
  Expects datasource, and a PreparedStatement"
  [ds stmt]
  (jdbc/execute! ds stmt))

(defn execute-one!
  "Call next.jdbc's `execute-one!`
  Expects datasource, and a PreparedStatement
  Returns a single result"
  [ds stmt]
  (jdbc/execute-one! ds stmt))

(defn get-datasource
  "Fetches the datasource given in configuration map."
  [spec]
  (jdbc/get-datasource spec))