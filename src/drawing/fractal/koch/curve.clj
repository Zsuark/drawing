(ns drawing.fractal.koch.curve
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [drawing.fractal.koch.snowflake :as koch]))

(def sWidth  500)
(def sHeight 200)


(defn setup-curve
  "Set a hashmap of lines to be a single line"
  []
  (let [x1  7
        y1  165
        x2  493
        y2  y1
        line1 (koch/makeLine (koch/makePoint x1 y1) (koch/makePoint x2 y2))]
    {:lines (list line1)}))

(defn update-state
  "Given a state, generation the next iteration"
  [state]
  (let [firstLine (first (:lines state))
        length    (koch/distance firstLine)]
    #_ "if the lines are too small, restart"
    (if (> length 3)
      (koch/update-state state)
      (do (q/no-loop) state))))

(defn setup
  "set frame rate and setup state"
  []
  (q/frame-rate 0.6)
  (setup-curve))

(defn run
  [host]
  (q/sketch
    :title "Koch Curve"
    :host host
    :size [sWidth sHeight]
    :setup setup
    :update update-state
    :draw koch/draw-state
    :middleware [m/fun-mode]))

(defn -main
  []
  (q/sketch
    :title "Koch Curve"
    :host "koch"
    :size [sWidth sHeight]
    :setup setup
    :update update-state
    :draw koch/draw-state
    :middleware [m/fun-mode]))
