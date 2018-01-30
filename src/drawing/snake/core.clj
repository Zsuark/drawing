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
  Returns a list of wall coordinates"
  []
  (let [border (get-border)
        holes  (generate-holes)]
    (filter #(not (contains? holes %)) border)))

(defn init-state []
  (let [direction     (random-direction)
        initial-state {
                       :snake (list [
                                     (- (/ (q/width) 2)  (* (rand-int 2) 10))
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


(defn add-apple [state really-add]
  (if really-add
    (let [old-apples (set (:apples state))
          wall       (set (:wall state))
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
            (update-in state [:apples] #(conj % new-apple))))))
    state))



; Setup - returns initial state
; Colour palette: http://www.colorhunt.co/c/106719
(defn setup []
  ; (println "Available fonts:" (q/available-fonts))
  (q/frame-rate 10)
  (q/stroke 0xff3090a1)
  (q/stroke-weight 2)
  (q/fill 0xff7bcecc)
  (q/text-font (q/create-font "Arial-BoldMT" 10))
  (add-apple (init-state) true))


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


; gives new state from state passed in
; Accepts current state
; Gives new state
(defn update-state [state]
  ; (println "snake:" (:snake state))
  ; (println "direction:" (:direction state))
  ; (println "last-direction:" (:last-direction state))
  (let [direction       (:direction state)
        [head-x head-y] (first (:snake state))
        new-x (case direction
                :left  (mod (- head-x 10) (q/width))
                :right (mod (+ head-x 10) (q/width))
                head-x)
        new-y (case direction
                :up   (mod (- head-y 10) (q/height))
                :down (mod (+ head-y 10) (q/height))
                head-y)
        
        new-head [new-x new-y]
        
        is-growing (> (:growing state) 0)
        
        apples (:apples state)
        
        ; if there are no apples there is a
        ; 1 in 5 chance that we will add one
        adding-apple (and
                       (zero? (count apples))
                       (zero? (rand-int 5)))
        
        apple-eaten (contains? apples new-head)
        old-score   (:score state)
        new-score   (if apple-eaten
                      (inc old-score)
                      old-score)
        
        new-wall    (if (and
                          (not= old-score new-score)
                          (zero? (mod new-score 3)))
                      (generate-wall)
                      (:wall state))
        
        
        new-state  (-> state
                       (update-in [:snake] #(cons
                                              new-head
                                              (if is-growing
                                                %
                                                (drop-last %))))
                       (update-in [:growing] (fn [g]
                                               (if is-growing
                                                 (dec g)
                                                 g)))
                       (assoc-in [:last-direction] direction)
                       (assoc-in [:wall] new-wall)
                       (assoc-in [:score] new-score)
                       
                       (update-in [:apples] #(if apple-eaten
                                               (disj % new-head)
                                               %))
                       
                       (update-in [:growing] #(if apple-eaten
                                                (+ % 2)
                                                %))
                       
                       (add-apple adding-apple))
        
        
        
        old-snake  (:snake state)
        new-snake  (:snake new-state)
        wall       (:wall  state)]
    ; (println "new-state:" new-state)
    (if (> new-score @high-score)
      (reset! high-score new-score))
    ; Check if the snake has hit itself
    ; reset the game if it has
    ; (if (not=
    ;       (count (set new-snake))
    ;       (count new-snake))
    (if apple-eaten
      new-state
      (if (contains? (set (rest new-snake)) new-head)
        (init-state)
        ; check if the snake has hit the wall
        ; reset the game if it has
        (if (contains? (set wall) new-head)
          (init-state)
          new-state)))))

(defn handle-keypress [state event]
  ; (println "event:" event)
  (let [key (:key event)]
    (if (and
          (contains? directions key)
          (change-is-legal key (:last-direction state)))
      (assoc-in state [:direction] key)
      state)))


(defn run [title]
  (q/sketch 
    :size [500 300]
    :title title
    :setup setup
    :draw draw
    :update update-state
    :key-pressed handle-keypress
    :middleware [m/fun-mode]))

(defn -main [& args]
  (run "Snake Game"))

