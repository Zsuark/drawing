; Sierpinski Curve Fractal snowflake????
(ns drawing.fractal.sierpinski.snowflake
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


#_ "TODO: These elements should be configurable by the webpage"

(def sWidth  650)
(def sHeight 650)



(defn
  makeCurve
  "
  Given an initial line, split it into three lines - represented by 4 points
  "
  [line]
  (let [
        [A B]   line        
        [Ax Ay] A
        [Bx By] B
        
        d           (distance A B)
        dx          (dx A B)
        dy          (dy A B)
        new-d       (/ d 2)
        
        theta1      (q/atan2 dy dx)
        theta2      (- theta1 (/ q/PI 3))
        
        Cx          (+ (* new-d (q/cos theta2)) Ax)
        Cy          (+ (* new-d (q/sin theta2)) Ay)
        C 			    [Cx Cy]
        
        Dx          (+ (* new-d (q/cos theta1)) Cx)
        Dy          (+ (* new-d (q/sin theta1)) Cy)
        D           [Dx Dy]]
    [A C D B]))


(defn
  setup-curve
  "
  Create an initial base line for the curve
  Assumes a 10 point border around the edges
  "
  []
  (let [
        Ax 150
        Bx (- sWidth  150)
        ; Ay (- sHeight 150)
        Ay (/ sHeight 2)
        By Ay
        A [Ax Ay]
        B [Bx By]]
    (concat
      (makeCurve [A B])
      (makeCurve [B A]))))

(defn 
  setup
  "Setup quil and initial state for drawing"
  []
  (q/frame-rate 0.6)
  (let [curves (setup-curve)]
    {:curves curves
     :inner (let [
                  y          (/ sHeight 2)
                  half-width (/ sWidth 8)
                  mid-x      (/ sWidth 2)
                  Ax         (- mid-x half-width)
                  Bx         (+ mid-x half-width)
                  A          [Ax y]
                  B          [Bx y]
                  ]
              (concat (makeCurve [A B]) (makeCurve [B A])))
     :pause pauseFrames}))

(defn update-state
  "Given a state, generation the next iteration"
  [state]
  
  (let [[A B] (take 2 (:curves state))
        length  (distance A B)]
    #_
    "If the length of the first line is less than 3, it means that it is
    now difficult to display any further line details.
    
    So, lets continue if line length is greater than 3, otherwise
    let's reset and start again."
    (if
      (> length 3)
      (assoc state
        :curves
        (mapcat makeCurve (partition 2 1 (:curves state)))
        :inner
        (mapcat makeCurve (partition 2 1 (:inner  state))))
      (if (> (:pause state) 0)
        (update state :pause dec)
        (setup)))))

(defn draw-state
  "Redraw the screen according to state"
  [state]
  
  (q/background 255)
  (q/stroke 0 125 255)
  (q/fill 255)
  (q/stroke-weight 1)
  (if (< 10 (count (:curves state)))
    (let [x-mid (/ sWidth 2)
          y-mid (/ sHeight 2)
          height (- (/ sWidth 4) 10)]
      (q/ellipse x-mid y-mid height height)))
  (q/fill 0 125 255)
  (q/stroke-weight (let [c (count (:curves state))]
                     (println "count:" c)
                     (cond
                       (< c 40) 8
                       (< c 6829) 4
                       :else 2)))
  
  (doseq [ [[Ax Ay] [Bx By]] (partition 2 1 (:curves state)) ]
    (q/line Ax Ay Bx By))
  (q/stroke-weight (if (< (count (:curves state)) 6829)
                     2
                     1))
  (doseq [ [[Ax Ay] [Bx By]] (partition 2 1 (:inner  state)) ]
    (q/line Ax Ay Bx By)))


(defn run [host]
  (q/sketch
    :host host
    :title "Sierpinski Snowflake ?"
    :size [sWidth sHeight]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

(defn -main
  []
  (run "sierpinski"))