(ns drawing.circle.modulo-multiplication
  (:require [quil.core :as q]
            [quil.middleware :as m]))

#_ "These elements should be configurable"
(def sWidth  800)
(def sHeight 800)
(def minDimension (min sWidth sHeight))
(def cNumberPoints (int (/ minDimension 4)))
(def cRadius (int (- (/ minDimension 2) 10)))
(def cStartingPoint q/PI)
(def sCentrePoint [(/ sWidth 2) (/ sHeight 2)])
(def circleColour {:r 0 :g 125 :b 75})
(def pointColour {:r 255 :g 75 :b 75})
(def lineColour {:r 255 :g 255 :b 255})
(def highlightColor {:r 255 :g 125 :b 75})
(def changesPerFrame 15)



; Function to calculate a list of points on a given circle
(defn getPoints
  ; generates n number of points
  ; on the circumference of the circle
  ; given by the radius and centre offset
  ; n - number of points
  ; r - radius
  ; h - x-offset from centre
  ; k - y-offset from centre
  [ n r [h k]]
  (let  [tau q/TWO-PI
         startingPoint cStartingPoint
         angleList (iterate #(+ % (/ tau n)) startingPoint) ]
    (map (fn [ angle ]
           (let [
                 x (+ (* r (q/cos angle)) h)
                 y (+ (* r (q/sin angle)) k) ]
             [ x y ]
             )) (take n angleList))))

(def points (into [] (getPoints cNumberPoints cRadius sCentrePoint)))

(def radiiPoints
  (into []
        (getPoints cNumberPoints (max sWidth sHeight) sCentrePoint)))


(defn drawConnections
  [ points connections rgb ]
  (q/stroke (:r rgb) (:g rgb) (:b rgb))
  (q/stroke-weight 1)
  (doseq [ l connections ]
    (let [ i1 (get l 0) i2 (get l 1)
          p1 (get points i1) p2 (get points i2)]
      (q/line p1 p2))))

(defn drawRadii
  [ points centre rgb ]
  (q/stroke (:r rgb) (:g rgb) (:b rgb))
  (doseq [p points] (q/line p centre)))


(defn setup-state []
  (q/frame-rate 90)
  ; {:multiplier 66
  ; {:multiplier 96
  ; {:multiplier (- cNumberPoints 2)
  {:multiplier (rand-int cNumberPoints)
   :connections [[0 0]]
   :currentPoint 0
   :connectionPoint 0
   :animate false
   :singleAnimation true})


(defn update-state [state]
  ; if we are animating - return the state update
  (if (:animate state)
    (let [
          oldCurrentPoint     (:currentPoint state)
          newCurrentPoint     (mod (inc oldCurrentPoint) cNumberPoints)
          oldMultiplier       (:multiplier state)
          newMultiplier       (if (zero? newCurrentPoint)
                                (if (= oldMultiplier (dec cNumberPoints))
                                  cNumberPoints
                                  (mod (inc oldMultiplier) cNumberPoints))
                                oldMultiplier)
          newAnimate          (if (:singleAnimation state)
                                (not (zero? newCurrentPoint))
                                (:animate state))
          newConnectionPoint  (mod
                                (* newCurrentPoint newMultiplier)
                                cNumberPoints)
          oldConnections      (:connections state)
          newConnections      (assoc oldConnections
                                newCurrentPoint
                                [newCurrentPoint newConnectionPoint])]
      (assoc state
        :multiplier       newMultiplier
        :connections      newConnections
        :currentPoint     newCurrentPoint
        :connectionPoint  newConnectionPoint
        :animate          newAnimate))
    
    ; Otherwise we just return the old state
    state))


(defn multiUpdate [state]
  (last (take changesPerFrame (iterate update-state state))))

(defn draw-state [state]
  (q/background 0)
  (drawRadii radiiPoints sCentrePoint pointColour)
  
  (q/stroke 255 0 0)
  (q/fill 255 0 0)
  (q/stroke-weight 4)
  (q/text
    (str "multiplier: " (let [m (:multiplier state)
                              c (:currentPoint state)]
                          (if (and (zero? c) (> m 0))
                            (dec m)
                            m)))
    
    10 10 100 100)
  (q/text
    (str "current point: " (:currentPoint state)) 10 30 100 100)
  (q/text
    (str "total points: " cNumberPoints) 10 50 100 100)
  
  (drawConnections points (:connections state) lineColour)
  
  ; Draw the current chord in
  (q/stroke 255)
  (q/stroke (:r highlightColor) (:g highlightColor)
            (:b highlightColor))
  (q/stroke-weight 1)
  (let [
        l (get (:connections state) (:currentPoint state))
        i1 (get l 0)        i2 (get l 1)
        p1 (get points i1)  p2 (get points i2)
        ]
    (q/line p1 p2)))

(defn toggleAnimation [state event]
  "Stops or continues the animation"
  (update state :animate #(not %)))

(defn checkForSpace [state event]
  "Stops or continues animation when space is pressed"
  (let [keyCode (:key-code event)
        spacePressed (= keyCode 32)]
    (if spacePressed
      (toggleAnimation state event)
      state)))

(defn
  -main
  []
  (q/sketch
    :title         "Modulo Multiplication"
    :host          "modulo-multiplication"
    :size          [sWidth sHeight]
    :setup         setup-state
    :update        multiUpdate
    :draw          draw-state
    :mouse-clicked toggleAnimation
    :key-pressed   checkForSpace
    :middleware    [m/fun-mode]))
