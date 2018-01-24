(ns drawing.fractal.dragon
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.math.numeric-tower :as maths]
            [clojure.core.match :refer [match]]))

; Good explanation of the dragon curve:
;   - https://bentrubewriter.com/2012/04/25/fractals-you-can-draw-the-dragon-curve-or-the-jurassic-fractal/

#_ "TODO: These elements should be configurable by the webpage or the app"

(def sWidth  650)
(def sHeight 650)

(def start-direction "Starting direction in terms of π" 1)
(def max-iterations  "Maximum number of iterations before resetting" 18)
(def starting-line-size 2)
(def fps "Frames per second to try to run at" 20)
(def pause-frames "Additional frames to pause for at the end" 0)

(defn
  setup-state
  "Create an initial state"
  []
  {:instruction-list (list :F)
   :iteration    0
   :unit-length  starting-line-size
   :pause        (inc pause-frames)})



(defn reverse-instructions
  "Given a list of instructions, reverse the list - swapping left and right"
  [instruction-list]
  (map
    #(match [%]
            [:L] :R
            [:R] :L
            :else %)
    (reverse instruction-list)))


(defn iterate-instructions
  "Given a list of instructions, generate the next iteration.
  The next iteration is a duplication of the last instructions, turned
  1/2 Pi (90 degrees) to the left/anti-clockwise"
  [instruction-list]
  (concat instruction-list (list :L) (reverse-instructions instruction-list)))


#_
"If the length of a line is less than 3, it means that it is
now difficult to display any further line details.

So, lets continue if line length is greater than 3, otherwise
let's reset and start again."

(def update-state
  "Given a state, generation the next iteration"
  (memoize
    (fn [state]
      (let [iteration (:iteration state)
            new-state (if (< iteration max-iterations)
                        (-> state
                            (update :instruction-list iterate-instructions)
                            (update :iteration inc))
                        (if (> (:pause state) 0)
                          (update state :pause dec)
                          (setup-state)))]
        new-state))))

(defn
  turn
  "Turns the angle if needed, according to instruction.
  :L - left (counterclockswise) turn by 1/2 π
  :R - right (clockswise) turn by 1/2 π
  Ignore anything else, and return the original angle
  "
  [angle instruction]
  (match
    [instruction]
    [(:or :L :R)] (let [newAngle        (if (= instruction :L)
                                          (- angle 1/2)
                                          (+ angle 1/2))
                        simplifiedAngle (if (= 3/2 (maths/abs newAngle))
                                          (if (> newAngle 0)
                                            -1/2
                                            1/2)
                                          newAngle)]
                    simplifiedAngle)
    :else angle))




(def make-points
  "
  Takes state, returns the resulting lines
  as a list of points.
  
  Instuctions:
  :F means forward by one unit
  :L means turn left (counter-clockwise) by 1/2 Pi
  :R means turn right (clockwise) by 1/2 Pi
  "
  (fn [state]
    (let [innerFunc
          (memoize
            (fn [state]
              (loop [instruction-list (:instruction-list state)
                     distance         (:unit-length state)
                     angle            start-direction
                     acc              [[(/ sWidth 2) (/ sHeight 2)]]]
                (if (empty? instruction-list)
                  acc
                  (let [instruction (first instruction-list)
                        remaining   (rest  instruction-list)]
                    (match [instruction]
                           
                           [(:or :L :R)] (recur
                                           remaining distance (turn angle instruction) acc)
                           
                           [:F]          (let [theta    (* angle q/PI)
                                               distance (:unit-length state)
                                               [x1 y1]  (last acc)
                                               x2       (+ (* distance (q/cos theta)) x1)
                                               y2       (+ (* distance (q/sin theta)) y1)
                                               newAcc   (conj acc [x2 y2])]
                                           (recur remaining distance angle newAcc))))))))
          newPoints (innerFunc state)]
      newPoints)))


(defn
  draw-state
  "Redraw the screen according to state"
  [state]
  (q/background 255)
  (q/stroke 0 125 255)
  (q/stroke-weight 1)
  (let [points (make-points state)]
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