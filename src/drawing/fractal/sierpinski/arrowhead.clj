; Sierpinski Curve Fractal snowflake????
(ns drawing.fractal.sierpinski.arrowhead
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.math.numeric-tower :as maths]))

; How many frames to pause at end of animation before restarting
(def pause-frames 5)



#_ "TODO: These elements should be configurable by the webpage"

(def sWidth  650)
(def sHeight 566)


(defn
  setup-state
  "Create an initial state"
  []
  {:instruction-list (list :A)
   :iteration    0
   :unit-length  (- sWidth 20)
   :pause        0})


(defn
  line-length
  "Gets the line length of a given state"
  [state]
  (let [unit-length (:unit-length state)
        iteration   (:iteration   state)
        denominator (maths/expt 2 iteration)
        length      (* unit-length (/ 1 denominator))]
    length))



(defn
  update-item
  "Given an item, will return an expanded list of items, according to the rules"
  [item]
  (case item
    :A (list :B :- :A :- :B)
    :B (list :A :+ :B :+ :A)
    (list item)))


#_
"If the length of a line is less than 3, it means that it is
now difficult to display any further line details.

So, lets continue if line length is greater than 3, otherwise
let's reset and start again."

(def update-state
  "Given a state, generation the next iteration"
  (memoize
    (fn [state]
      (let [length    (line-length state)
            new-state (if (> length 2)
                        (-> state
                            (update :instruction-list #(mapcat update-item %))
                            (update :iteration inc))
                        (if (> (:pause state) 0)
                          (update state :pause dec)
                          (assoc (setup-state) :pause pause-frames)))]    
        new-state))))

(defn
  turn
  "Turns the angle if needed, according to instruction.
  Angle: ratio of pi in 1/3 steps, e.g. -2/3, 1/3
  Results > 1 will be simplified - e.g. 4/3 -> -2/3, -4/3 -> 2/3
  Instruction: :A :B :+ :-
  :A or :B - does nothing, returns original angle
  :+ means turn left  by 1/3
  :- means turn right by 1/3
  NB: Be warned, the L-system instruction may be appear counter-intuitive.
  A left turn (anti-clockwise) is in a minus direction for
  our cartesian plane, a right (clockwise) is a plus direction.
  "
  [angle instruction]
  (if (or (= instruction :A) (= instruction :B))
    angle
    (let [newAngle        (if (= instruction :+)
                            (- angle 1/3)
                            (+ angle 1/3))
          simplifiedAngle (if (= 4/3 (maths/abs newAngle))
                            (if (> newAngle 0)
                              -2/3
                              2/3)
                            newAngle)]
      simplifiedAngle)))


(def make-points
  "
  Takes state, returns the resulting lines
  as a list of points.
  
  Instuctions:
  :A or :B means forward by one unit
  :+ means turn left  by 1/3 Pi
  :- means turn right by 1/3 Pi
  "
  (memoize
    (fn [state]
      (loop [instruction-list (:instruction-list state)
             distance         (line-length state)
             angle            (if
                                (zero? (mod (:iteration state) 2))
                                0
                                -1/3)
             acc              [[10 (- sHeight 10)]]
             ]
        (if (empty? instruction-list)
          acc
          (let [instruction (first instruction-list)
                remaining   (rest  instruction-list)
                newAngle    (turn angle instruction)
                ]
            (if (or (= instruction :+) (= instruction :-))
              (recur remaining distance newAngle acc)
              (let [theta    (* angle q/PI)
                    distance (line-length state)
                    [x1 y1]  (last acc)
                    x2       (+ (* distance (q/cos theta)) x1)
                    y2       (+ (* distance (q/sin theta)) y1)
                    newAcc   (conj acc [x2 y2])]
                (recur remaining distance newAngle newAcc)))))))))


(defn nowString
  []
  (.toString (java.util.Date.)))

(defn
  draw-state
  "Redraw the screen according to state"
  [state]

  (q/background 255)
  
  ; Uncomment to display simple stats
  ; (q/fill 0 125 255)
  ; (q/text (str "current fps:" (q/current-frame-rate)) 10 100)
  ; (q/text (str "Time is:" (nowString)) 10 120)
  
  (q/stroke 0 125 255)
  (q/fill 255)
  (q/stroke-weight 1)
  
  (let [points (make-points state)]
    (doseq [[[x1 y1] [x2 y2]] (partition 2 1 points)]
      (q/line x1 y1 x2 y2))))

(defn 
  setup
  "Setup quil and initial state for drawing"
  []
  (q/frame-rate 4)
  (setup-state))
  ; (let [all-states (take 12 (iterate update-state (setup-state)))
  ;       all-points (map make-points all-states)]
  ;   (last all-points)
  ;   (setup-state)))


(defn run [host]
  (q/sketch
    :host host
    :title "Sierpinski Arrowhead"
    :size [sWidth sHeight]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

(defn -main
  []
  (run "sierpinski"))