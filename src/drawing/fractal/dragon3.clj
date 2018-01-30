(ns drawing.fractal.dragon3
  (:require [quil.core :as q]
            [quil.middleware :as m]))

; Good explanation of the dragon curve:
;   - https://bentrubewriter.com/2012/04/25/fractals-you-can-draw-the-dragon-curve-or-the-jurassic-fractal/

#_ "TODO: These elements should be configurable by the webpage or the app"

(def sWidth  650)
(def sHeight 650)

(def max-iterations  "Maximum number of iterations before resetting" 17)
(def fps "Frames per second to try to run at" 1)
(def pause-frames "Additional frames to pause for at the end" 5)

(def unit-length 2)
(def x-origin (int (/ sWidth 2)))
(def y-origin (int (/ sHeight 2)))

(def initial-state {:point-list  [[x-origin y-origin]
                                  [(- x-origin unit-length) y-origin]]
                    :iteration   0
                    :pause-count (inc pause-frames)})

(def
  rotatePoint
  "Rotate a point around a given axis"
  (memoize
    (fn
      [[x-origin y-origin] [x y]]
      (let [offset-x (- x-origin x)
            offset-y (- y-origin y)
            inter-x  offset-y
            inter-y  (- offset-x)
            new-x    (+ x-origin inter-x)
            new-y    (+ y-origin inter-y)
            ]
        [new-x new-y]))))


(def iterate-points
  "Given a list of instructions, generate the next iteration.
  The next iteration is a duplication of the last instructions, turned
  1/2 Pi (90 degrees) to the left/anti-clockwise"
  (memoize
    (fn [point-list]
      (let [rotation-point (last point-list)
            points-to-rotate (rest (reverse point-list))
            rotate           (partial rotatePoint rotation-point)]
        (concat
          point-list
          (map rotate points-to-rotate))))))


(def update-state
  "Given a state, generation the next iteration"
  (memoize
    (fn [state]
      (let [iteration (:iteration state)
            new-state (if (< iteration max-iterations)
                        (-> state
                            (update :point-list iterate-points)
                            (update :iteration inc))
                        (if (> (:pause-count state) 0)
                          (update state :pause-count dec)
                          initial-state))]
        new-state))))


(defn
  draw-state
  "Redraw the screen according to state"
  [state]
  (q/background 255)
  (q/stroke 0 125 255)
  (q/stroke-weight 1)
  (doseq [[[x1 y1] [x2 y2]] (partition 2 1 (:point-list state))]
    (q/line x1 y1 x2 y2)))

(defn
  setup
  "Setup quil and initial state for drawing"
  []
  (q/frame-rate fps)
  initial-state)


(defn run [host]
  (q/sketch
    :title "Dragon Curve"
    :size [sWidth sHeight]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

(defn -main
  []
  (run "dragon"))