; Wallis Seive Cross - oops!
; 
; I made a mistake, but the result is still an interesting
; fractal (discontinuous fractal?)
; Actual has a different name by mathematics - can you find out what it is?
(ns drawing.fractal.wallis.seive-cross
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
  Divides a given square into (2i + 1)^2.
  Input: Square - vector of two points and a height [x y h]
  Input: i - iteration number (natural number)
  Output: (2i + 1)^2 squares in a vector
  NB:
  - Square side length must be (2i + 1)
  - As the middle section is removed, we must
  be able to figure out the mid-point.
  The intersection of x and y mid-points is the centre
  section to be removed
  - hmm.....  i = 1, divisor = 3, 3/2 = 1.5, therefore the 2nd value of
  X: {1, 2, 3}, Y: {1, 2, 3}. So, square (2, 2) is to be skipped.
  All possible squares: {(1,1) (1,2) (1, 3) (2,1) (2,2) (2,3)
  (3,1) (3,2) (3,3)})
  - i = 1, squares 0,1 and 2,3 remain. Square 1,2 removed
  - i = 4, divisor 9. Square 4,5 removed
  - i = 9, divisor 19. Square 9,10 removed.
  - for any value of i, square i,i+1 is removed
  
  - original square 0,0 to 9,9  (aka 0,3)
  - i = 1, divisor = 3
  - new-size: 3
  - new squares
  (q/rect 0 0 3 3)
  (q/rect 3 0 3 3)
  (q/rect 6 0 3 3)
  
  (q/rect 0 3 3 3)
  ; (q/rect 3 3 3 3) REMOVED FOR HOLE
  (q/rect 6 3 3 3)
  
  (q/rect 0 6 3 3)
  (q/rect 3 6 3 3)
  (q/rect 6 6 3 3)    
  "
  [Square i]
  (let [divisor (inc (* 2 i))
        [x y h] Square
        size    (/ h divisor)
        x-mid   (+ x (* i size))
        y-mid   (+ y (* i size))
        x-range (range x (+ x h) size)
        y-range (range y (+ y h) size)
        newSquares (for [
                         x x-range
                         y y-range
                         :when (and (not= x x-mid) (not= y y-mid))]
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
    (if
      (> h 6)
      (let [i (inc (:iteration state))
            oldSquares (:squares state)
            newSquares (mapcat #(divideSquare % i) oldSquares)]
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



(defn run [host]
  (q/sketch
    :host host
    :title "Wallis Seive"
    :size [sWidth sHeight]
    :setup setup
    :update update-wrapper
    :draw draw-state
    :middleware [m/fun-mode]))

(defn -main
  []
  (run "sieve"))