(ns drawing.fractal.vicsek
  (:require [quil.core :as q]
            [quil.middleware :as m]))

; How many frames to pause at end of animation before restarting
(def pauseFrames 5)


(defn divideSquare
  "
  "
  [Square]
  (let [divisor 3
        [x y h] Square
        size    (/ h divisor)
        x-mid   (+ x size)
        y-mid   (+ y size)
        x-range (range x (+ x h) size)
        y-range (range y (+ y h) size)
        newSquares (for [
                         x x-range
                         y y-range
                         :when (not (and (not= x x-mid) (not= y y-mid)))]
                     [x y size])]
    newSquares))


; use the height as the width with q/rect to make a square
(defn drawSquare
  "Uses quil to draw a triangle on the screen."
  [Square]
  (let [[x y h] Square]
    (q/rect x y h h)))

#_ "TODO: These elements should be configurable by the webpage"

(def sWidth  620)
(def sHeight 620)


(defn
  setup-square
  "
  Create an initial square.
  Assumes a 10 point border around the edges
  "
  []
  (let
    [minDimension (- (min sWidth sHeight) 20)]
    (vector 10 10 minDimension)))


(defn
  setup-state
  "Generate initial state"
  []
  {:squares (list (setup-square))
   :iteration 0
   :pause pauseFrames})

(defn 
  setup
  "Setup quil and initial state for drawing"
  []
  (q/frame-rate 0.6)
  (setup-state))


(defn update-state
  "Given a state, generation the next iteration"
  [state]
  (let [[x y h] (first (:squares state))]
    #_
    "If the length of the first line is less than 3, it means that it is
    now difficult to display any further line details.
    
    So, lets continue if line length is greater than 3, otherwise
    let's reset and start again."
    ; (println "+++ h:" h)
    (if
      (>= h 1.5)
      (let [i (inc (:iteration state))
            oldSquares (:squares state)
            newSquares (mapcat divideSquare oldSquares)]
        (assoc state 
          :squares   newSquares
          :iteration i))
      (if (> (:pause state) 0)
        (update state :pause dec)
        (setup-state)))))

(defn update-wrapper
  [state]
  (let [newState (update-state state)]
    newState))

(defn draw-state
  "Redraw the screen according to state"
  [state]
  (q/background 0xffffffff)
  (q/stroke 0xffff0033)
  (q/fill   0xffff0033)
  (q/stroke-weight 1)
  (doseq
    [Square (:squares state)]
    (drawSquare Square)))



(defn run [host]
  (q/sketch
    :title "Vicsek Fractal"
    :size [sWidth sHeight]
    :setup setup
    :update update-wrapper
    :draw draw-state
    :middleware [m/fun-mode]))

(defn -main
  []
  (run "vicsek"))