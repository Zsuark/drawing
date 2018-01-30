; I made a mistake, with Wallis' Seive, but the result is still an interesting
; fractal (discontinuous fractal?)
; Actual has a different name by mathematics - can you find out what it is?
(ns drawing.fractal.cantor.dust
  (:require [quil.core :as q]
            [quil.middleware :as m]))

; How many frames to pause at end of animation before restarting
(def pauseFrames 5)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The main methods and helpers continue from here.
;;
;; The main transformation takes a given square and splits
;; it into five smaller squares.
;;

(defn divideSquare
  "
  Divides a square into 4 smaller squares,
  each with a size 1/3 the original size.
  "
  [Square]
  (let [divisor 3
        [x y h] Square
        size    (/ h divisor)
        step    (- h size)
        x-range (range x (+ x h) step)
        y-range (range y (+ y h) step)
        newSquares (for [x x-range
                         y y-range]
                     [x y size])]
    newSquares))


; use the height as the width with q/rect to make a square
(defn drawSquare
  "Uses quil to draw a square on the screen."
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
    "If the length of the first line is less than 1, it means that it is
    now difficult to display any further line details.
    
    So, lets continue if line length is greater than 1, otherwise
    let's reset and start again."
    (if
      (> h 1)
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
  (q/background 255)
  (q/stroke 0xff3366FF)
  (q/fill   0xff3366FF)
  (q/stroke-weight 1)
  (doseq
    [Square (:squares state)]
    (drawSquare Square)))



(defn run []
  (q/sketch
    :title "Cantor Dust"
    :size [sWidth sHeight]
    :setup setup
    :update update-wrapper
    :draw draw-state
    :middleware [m/fun-mode]))

(defn -main
  []
  (run))