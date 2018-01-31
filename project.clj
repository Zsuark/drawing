(defproject drawing "0.1.0-SNAPSHOT"
  :description "Some random Quil drawings"
  :url "http://github.com/Zsuark/drawing"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [quil "2.6.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [org.clojure/clojurescript "1.9.946"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.14"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds [
                       ; {:id "development"
                       ;  :source-paths ["src-cljs"]
                       ;  :figwheel true
                       ;  :compiler
                       ;  {:main "drawing.snake.core"
                       ;   :output-to "resources/public/js/main.js"
                       ;   :output-dir "resources/public/js/development"
                       ;   :asset-path "js/development"}}
                       ; minified and bundled build for deployment
                       {:id "optimized"
                        :source-paths ["src-cljs"]
                        :compiler
                        {:main "drawing.snake.core"
                         :output-to "resources/public/js/snake.js"
                         :output-dir "resources/public/js/optimised"
                         :asset-path "js/optimized"
                         :optimizations :advanced}}]}  
  :aot :all
  :main drawing.menu.core)
