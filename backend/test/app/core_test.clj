(ns app.core-test
  (:require [clojure.test :refer [deftest is testing]]))

(deftest helloworld-test
   (testing "Hello World Test!"
      (is (= 1 1))))
