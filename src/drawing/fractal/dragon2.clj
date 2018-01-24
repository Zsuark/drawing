(ns drawing.fractal.dragon2
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.math.numeric-tower :as maths]
            [clojure.core.match :refer [match]]))


#_ "TODO: These elements should be configurable by the webpage"

(def sWidth  650)
(def sHeight 650)

(def max-iterations  "Maximum number of iterations before resetting" 18)
(def starting-line-size 1)
(def fps "Frames per second to try to run at" 20)
(def pause-frames "Additional frames to pause for at the end" 4)

(def scale  "Unit length to scale lines to" 0.2)
(def offset "Starting position from origin"
  [(int (/ sWidth 2)) (int (/ sHeight 2))])

(defn
  setup-state
  "Create an initial state"
  []
  {:point-list   (list [0 0] [-1 0])
   :iteration    0
   :unit-length  starting-line-size
   :pause        (inc pause-frames)})



(def update-state
  "Given a state, generation the next iteration"
  (memoize
    (fn [state]
      (if (= (:iteration state) max-iterations)
        (if (zero? (:pause state))
          (setup-state)
          (update state :pause dec))
        (let [
              old-points (:point-list state)
              [rotate-x rotate-y] (last old-points)
              new-points (concat
                           old-points
                           (map
                             (fn [[x y]]
                               (let [
                                     y-offset (- x rotate-x)
                                     x-offset (- (- y rotate-y))
                                     new-x   (+ rotate-x x-offset)
                                     new-y   (+ rotate-y y-offset)
                                     ]
                                 [new-x new-y]))
                             (rest (reverse old-points))))]
          (-> state
              (assoc :point-list new-points)
              (update :iteration inc)))))))


(def scale-and-offset
  "Given a series of points, scale them by a unit length
  and apply an offset from the origin"
  (memoize
    (fn [points scale [x-offset y-offset]]
      (map (fn [point]
             (let [[x y] point]
               [(+ (* x scale) x-offset) (+ (* y scale) y-offset)]))
           points))))


(defn
  draw-state
  "Redraw the screen according to state"
  [state]
  (q/background 255)
  (q/stroke 0 125 255)
  (q/stroke-weight 1)
  (let [points (scale-and-offset
                 (:point-list state)
                 scale
                 offset)]
    (doseq [[[x1 y1] [x2 y2]] (partition 2 1 points)]
      (q/line x1 y1 x2 y2))))

(defn
  setup
  "Setup quil and initial state for drawing"
  []
  (q/frame-rate fps)
  (setup-state))


(defn run [host]
  (q/sketch
    :host host
    :title "Dragon"
    :size [sWidth sHeight]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

(defn -main
  []
  (run "dragon"))
