(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'app)
(def version "0.1.0")
(def main 'app.core)

(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file "target/app.jar")

(defn uber [_]
  (b/delete {:path "target"})
  (b/copy-dir {:src-dirs ["src" "resources"] :target-dir class-dir})
  (b/compile-clj {:basis basis :src-dirs ["src"] :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file jar-file
           :basis basis
           :main main}))