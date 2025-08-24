(ns app.router.core-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [app.test-util :refer [system-fixture request!]]))

(use-fixtures :once system-fixture)

(deftest backend-is-healthy
  (testing "ping"
    (let [{:keys [status body] :as request} (request! :get "/ping")]
      (is (= status 200))
      (is (= "pong" body)))))
