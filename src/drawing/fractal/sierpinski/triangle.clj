; Sierpinski Triangle Fractal
(ns drawing.fractal.sierpinski.triangle
  (:require [quil.core :as q]
            [quil.middleware :as m]))

; How many frames to pause at end of animation before restarting
(def pauseFrames 5)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Cartesian point maths
;;

(defn dx
  "distance of x coordinates between the two points of a line"
  [A B]
  (let [[Ax Ay] A
        [Bx By] B]
    (- Bx Ax)))

(defn dy
  "distance of y coordinates between the two points of a line"
  [A B]
  (let [[Ax Ay] A
        [Bx By] B]
    (- By Ay)))

(defn distance
  "Find the distance between two points"
  [A B]
  (let [dx (dx A B)
        dy (dy A B)]
    (q/sqrt (+ (* dx dx) (* dy dy)))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The main methods and helpers continue from here.
;;
;; The main transformation takes a given triangle and splits
;; it into three smaller triangle.
;;

(defn divideTriangle
  "
  Divides a given equilateral triangle into three.
  - Each point is an [x, y] coordinate
  Input: Three points A, B, C
  Output: Three triangles consisting of nine new points.
  - Triangle is written as a set of three points in a vector
  "
  [Triangle]
  (let
    [[A B C] Triangle
     [Ax Ay] A 
     [Bx By] B
     [Cx Cy] C
     ab      [(+ Ax (/ (dx A B) 2)) (+ Ay (/ (dy A B) 2))]
     bc      [(+ Bx (/ (dx B C) 2)) (+ By (/ (dy B C) 2))]
     ca      [(+ Cx (/ (dx C A) 2)) (+ Cy (/ (dy C A) 2))]]
    [[A ab ca]
     [ab B bc]
     [bc C ca]]))

(defn drawTriangle
  "Uses quil to draw a triangle on the screen."
  [Triangle]
  (let [[A B C] Triangle
        [Ax Ay] A 
        [Bx By] B 
        [Cx Cy] C]
    (q/triangle Ax Ay Bx By Cx Cy)))

#_ "TODO: These elements should be configurable by the webpage"

(def sWidth  650)
(def sHeight 566)



(defn
  makeTriangle
  "
  We need to make an initial equilateral triangle
  So, we take two points as a baseline.
  "
  [A B]
  (let [        
        [Ax Ay] A
        [Bx By] B                
        dx          (dx A B)
        dy          (dy A B)
        d           (distance A B)
        theta1      (q/atan2 dy dx)
        theta2      (- theta1 (/ q/PI 3))
        Cx          (+ (* d (q/cos theta2)) Ax)
        Cy          (+ (* d (q/sin theta2)) Ay)
        C 			[Cx Cy]]
    [A B C]))

(defn
  setup-triangle
  "
  Create an initial three points for the base triangle.
  Assumes a 10 point border around the edges
  "
  []
  (let
    [Ax 10
     Bx (- sWidth 10)
     Ay (- sHeight 10)
     By Ay
     A [Ax Ay]
     B [Bx By]]
    (vector (makeTriangle A B))))

(defn 
  setup
  "Setup quil and initial state for drawing"
  []
  (q/frame-rate 0.6)
  (let [triangles (setup-triangle)]
    {:triangles triangles
     :pause pauseFrames}))

(defn update-state
  "Given a state, generation the next iteration"
  [state]
  (let [[A B C] (first (:triangles state))
        length  (distance A B)]
    #_ "If the length of the first line is less than 3, it means that it is
       now difficult to display any further line details.
       
       So, lets continue if line length is greater than 3, otherwise
       let's reset and start again."
       (if
         (> length 3)
         (assoc state
           :triangles
           (mapcat divideTriangle  (:triangles state)))
         (if (> (:pause state) 0)
           (update state :pause dec)
             (hash-map
               :triangles (setup-triangle)
               :pause pauseFrames)))))

(defn draw-state
  "Redraw the screen according to state"
  [state]
  (q/background 255)
  (q/stroke 0 125 255)
  (q/fill 0 125 255)
  (q/stroke-weight 1)
  (doseq
    [[A B C] (:triangles state)]
    (drawTriangle [A B C])))



(defn run [host]
  (q/sketch
    :host host
    :title "Sierpinski Triangle"
    :size [sWidth sHeight]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

(defn -main
  []
  (run "sierpinski"))