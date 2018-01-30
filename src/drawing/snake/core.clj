(ns drawing.snake.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(comment
  "
  A snake is a collection of points representing squares on a grid.
  A snake grows by adding to the head.
  A snake moves by adding to the head and taking from the tail.
  
  Currently:
  - the plan is that each square a snake occupies is 10x10.
  - the snake starts in the centre position of the screen.
  - a hole is made in either the horizontal or vertical wall
  - the hole is between 1 and 5 units wide (each unit is 10 currently)
  
  - With every third apple, a new wall is generated with a new hole
  ")

(def high-score (atom 0))

; possible directions
(def directions (sorted-set :up :down :left :right))


(defn random-direction []
  (get (into [] directions) (rand-int 4)))

(defn get-border
  "
  Generates the coordinates along the border. Useful for making a wall.
  Returns a list of border coordinates.
  "
  []
  (for [x (range 0 (q/width) 10)
        y (range 0 (q/height) 10)
        :when (or
                (or (= y 0)
                    (= y (- (q/height) 10)))
                (or (= x 0)
                    (= x (- (q/width) 10))))]
    [x y]))

(defn generate-holes
  "
  Generates hole coordinates along the border
  Useful for when making a wall with a hole in it.
  Returns a set of hole coordinates
  "
  []
  (let [hole-is-horizontal (zero? (rand-int 2))
        hole-size  		   (inc (rand-int 5))
        coordinate 		   (* 10
                           (inc
                             (rand-int
                               (- 
                                 (/ (if hole-is-horizontal (q/width) (q/height)) 10)
                                 (+ 2 hole-size)))))
        hole-coordinates   (set
                             (for [x (if hole-is-horizontal
                                       (range
                                         coordinate
                                         (+ coordinate (* 10 hole-size))
                                         10)
                                       (list 0 (- (q/width) 10)))
                                   y (if hole-is-horizontal
                                       (list 0 (- (q/height) 10))
                                       (range
                                         coordinate
                                         (+ coordinate (* 10 hole-size))
                                         10))]
                               [x y]))]
    hole-coordinates))

(defn generate-wall
  "Generates a wall that covers the border, except for where the holes are.
  Returns a set of wall coordinates"
  []
  (let [border (get-border)
        holes  (generate-holes)]
    (set (filter #(not (contains? holes %)) border))))

(defn init-state []
  (let [direction     (random-direction)
        initial-state {
                       :snake (list
                                [(- (/ (q/width) 2)  (* (rand-int 2) 10))
                                 (- (/ (q/height) 2) (* (rand-int 2) 10)) ])
                       
                       :direction      direction
                       :last-direction direction
                       :growing 5
                       :apples #{}
                       :wall (generate-wall)
                       :score 0
                       }]
    ; (println "initial-state:" initial-state)
    initial-state))

(defn add-apple [state]
  (let [old-apples (:apples state)
        wall       (:wall state)
        snake      (set (:snake state))]
    (loop []
      (let [x (* (rand-int (/ (q/width)  10)) 10)
            y (* (rand-int (/ (q/height) 10)) 10)
            new-apple [x y]]
        (if (or
              (contains? old-apples new-apple)
              (contains? wall new-apple)
              (contains? snake new-apple))
          (recur)
          (update-in state [:apples] #(conj % new-apple)))))))



; Setup - returns initial state
; Colour palette: http://www.colorhunt.co/c/106719
(defn setup []
  ; (println "Available fonts:" (q/available-fonts))
  (q/frame-rate 10)
  (q/stroke 0xff3090a1)
  (q/stroke-weight 2)
  (q/fill 0xff7bcecc)
  (q/text-font (q/create-font "Arial-BoldMT" 10))
  (add-apple (init-state)))


; draws state it is given to the screen
; takes state, returns nothing
(defn draw [state]
  ; (println "state:" state)
  ; (println "direction:" (:direction state))
  
  ; background fill
  (q/background 0xfffef8e6)
  
  
  ; Draw the wall
  (q/with-stroke
    [0xff7bcecc]
    (doseq [[x y] (:wall state)]
      (q/rect x y 10 10)))
  
  
  ; Write the score and draw the apples
  (q/with-fill
    [0xffbc5148]
    (q/with-stroke
      [0xffbc5148]
      
      (q/text (str "SCORE: " (:score state)) 15 -1 200 15)
      (q/text (str "HIGH SCORE: " @high-score) 15 (- (q/height) 12) 200 (q/height))
      
      (doseq [[x y] (:apples state)]
        ; We need to offset the apple
        ; due to squares and ellipsees being
        ; drawn differently
        (q/ellipse (+ x 5) (+ y 5) 10 10))))
  
  ; Draw the snake
  ; First the head
  ; (q/with-stroke
  ;   [0xff7bcecc]
  (q/with-fill
    [0xff3090a1]
    (let [[x y] (first (:snake state))]
      (q/rect x y 10 10 4)))
    
  ; then the body
  (doseq [[x y] (rest (:snake state))]
    (q/rect x y 10 10 2)))


(defn change-is-legal [new-direction old-direction]
  (not
    (or 
      ; if any one of these below conditions are true
      ; the change in direction is not legal
      (and (= old-direction :left)
           (= new-direction :right))
      
      (and (= old-direction :right)
           (= new-direction :left))
      
      (and (= old-direction :up)
           (= new-direction :down))
      
      (and (= old-direction :down)
           (= new-direction :up)))))


(defn update-apple-state
  "
  Checks if an apple has been eaten.
  Updates score and removes eaten apple if needed.
  Updates the high score if needed.
  Every third apple resets the wall (and where the hole is)
  Returns state which has been updated as needed.
  "
  [state snake-head]
  (if (contains? (:apples state) snake-head)
    (let [new-state (-> state
                        (update :apples  #(disj % snake-head))
                        (update :growing #(+ 3 %))
                        (update :score   inc))
          new-score (:score new-state)]
      
      (if (> new-score @high-score)
        (reset! high-score new-score))
      
      ; (println "update-apple-state old state:" state)
      ; (println "update-apple-state new-state:" new-state)
      
      (if (zero? (mod (:score new-state) 3))
        (assoc new-state :wall (generate-wall))
        new-state))
    state))

(defn move-snake
  "
  Called when snake makes a successful move.
  Updates and returns state.
  
  If there are no apples, then there is a 1 in 5 chance of adding one.
  There are apples, then there is a 1 in (number of apples * 100) chance of adding one.
  "
  [state snake-head snake-tail]
  (let [apple-count  (count (:apples state))
        adding-apple (zero?
                       (rand-int (if (zero? apple-count) 5 (* 100 apple-count))))
        new-state    (-> (if adding-apple
                           (add-apple state)
                           state)
                         (assoc :snake (cons snake-head snake-tail)))]
    ; (println "move-snake (old) state:" state)
    ; (println "move-snake   new-state:" new-state)
    new-state))



; gives new state from state passed in
; Accepts current state
; Gives new state
(defn update-state
  "
  Checks if the snake has collided with itself or the wall
  If so, reset the game
  
  Otherwise:
  - amend the growth value if needed
  - update the last-direction to be the now current direction
  - check the apple situation
  - move the snakes position
  "
  [state]
  ; (println "state:" state)
  (let [direction       (:direction state)
        [head-x head-y] (first (:snake state))
        new-x           (case direction
                          :left  (mod (- head-x 10) (q/width))
                          :right (mod (+ head-x 10) (q/width))
                          head-x)
        new-y           (case direction
                          :up   (mod (- head-y 10) (q/height))
                          :down (mod (+ head-y 10) (q/height))
                          head-y)        
        is-growing      (> (:growing state) 0)
        new-head        [new-x new-y]                
        new-tail        (if is-growing
                          (:snake state)
                          (drop-last (:snake state)))
        wall            (:wall  state)
        new-state       (if (or
                              (contains? (set new-tail) new-head)
                              (contains? wall new-head))
                          (init-state)
                          (->
                            (if is-growing
                              (update state :growing dec)
                              state)
                            (assoc :last-direction direction)
                            (update-apple-state new-head)
                            (move-snake new-head new-tail)))]
    new-state))




(defn handle-keypress [state event]
  (let [key (:key event)]
    (if (and
          (contains? directions key)
          (change-is-legal key (:last-direction state)))
      (assoc-in state [:direction] key)
      state)))


(defn run []
  (q/sketch 
    :size [500 300]
    :title "Snake Game!"
    :setup setup
    :draw draw
    :update update-state
    :key-pressed handle-keypress
    :middleware [m/fun-mode]))

(defn -main [& args]
  (run))

