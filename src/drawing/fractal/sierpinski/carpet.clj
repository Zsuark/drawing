; Sierpinski Carpet Fractal
(ns drawing.fractal.sierpinski.carpet
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
  Divides a given square into eight (nine with the middle square
  missing).
  Input: Square - vector of two points and a height [x y h]
  Output: Eight squares in a vector
  "
  [Square]
  (let [[x y h] Square
        height  (/ h 3)
        x1      x
        x2      (+ x1 height)
        x3      (+ x2 height)
        y1      y
        y2      (+ y1 height)
        y3      (+ y2 height)]
    [[ x1 y1 height ][ x2 y1 height ][ x3 y1 height ]
     [ x1 y2 height ]                [ x3 y2 height ]
     [ x1 y3 height ][ x2 y3 height ][ x3 y3 height ]]))


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
  setup
  "Setup quil and initial state for drawing"
  []
  (q/frame-rate 0.6)
  (let
    [starting-square (setup-square)]
    {:squares (vector starting-square)
     :pause pauseFrames}))

(defn update-state
  "Given a state, generation the next iteration"
  [state]
  (let [[x y h] (first (:squares state))]
    #_ "If the length of the first line is less than 3, it means that it is
       now difficult to display any further line details.
       
       So, lets continue if line length is greater than 3, otherwise
       let's reset and start again."
       (if
         (> h 6)
         (assoc state
           :squares
           (mapcat divideSquare  (:squares state)))
         (if (> (:pause state) 0)
           (update state :pause dec)
           (hash-map
             :squares (vector (setup-square))
             :pause pauseFrames)))))

(defn draw-state
  "Redraw the screen according to state"
  [state]
  (q/background 255)
  (q/stroke 255 25 181)
  (q/fill 255 25 181)
  (q/stroke-weight 1)
  (doseq
    [Square (:squares state)]
    (drawSquare Square)))



(defn run [host]
  (q/sketch
    :host host
    :title "Sierpinski Carpet"
    :size [sWidth sHeight]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

(defn -main
  []
  (run "carpet"))