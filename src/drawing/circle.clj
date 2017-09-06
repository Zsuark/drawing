(ns drawing.circle
	(:require [quil.core :as q]))

; For this sketch we will animate
; drawing points around a circle,
; and then drawing lines between
; multiples (if you imagine the points being an
; numbered sequence)
; We will increase the multiplication
; in the image every x numbers of frames


; I am wanting to draw a circle and divide its circumference by
; n even sections marked by a point (where n is a natural number).

; Each segment will have an angle of (2*PI)/n radians
; or 360/n degrees

; https://stackoverflow.com/questions/5300938/calculating-the-position-of-points-in-a-circle
; Given a radius length r and an angle t in radians and a circle's center (h,k),
; you can calculate the coordinates of a point on the circumference as follows
; (this is pseudo-code, you'll have to adapt it to your language):
; float x = r*cos(t) + h;
; float y = r*sin(t) + k;

; We are going to set the:
; - the sketch width and height
; - number of points for a circle
; - The circle radius
; - The centre point of the sketch
; - the starting point of the circle
; (def cNumberPoints 200)
; (def cNumberPoints 400)
; (def cNumberPoints 500)
; (def sWidth  700)
; (def sHeight 700)
; (def cRadius 350)

(def sWidth  700)
(def sHeight 700)
(def minDimension (min sWidth sHeight))
(def cNumberPoints (int (/ minDimension 4)))
(def cRadius (int (- (/ minDimension 2) 10)))

; Starting point indicates the 0 point on a circle
(def cStartingPoint (rand q/TWO-PI))
(def sCentrePoint
  ((fn [w h]
    [(/ w 2) (/ h 2)]) sWidth sHeight))

(def circleColour {:r 75 :g 75 :b 255})
(def pointColour {:r 255 :g 75 :b 75})
(def lineColour {:r 0 :g 125 :b 75})

; Now we need a way to animate the multiplication tables
; So let's make a walker function to iterate from 1 to
; the number of points, and back again

(defn circular [min max]
  (let [
    spread (inc (- max min))
    counter (atom 0)
    ]
    (fn []
      (let [current (+ @counter min)]
        (swap! counter #(mod (inc %) spread))
        current))))



(defn walker [min max]
  (let [counter (atom min) direction (atom :up)]
    (fn []
      (let [
        current @counter
        isUp (= @direction :up)
        atMin (= current min)
        atMax (= current max)
        changeDown (and atMax isUp)
        changeUp (and atMin (not isUp))
        changeDirection (or changeUp changeDown)
        ]
        (if changeDirection
          (reset! direction (if changeDown :down :up))
          (swap! counter (if isUp inc dec)))
        current))))

(def pointIndexWalker (walker 1 cNumberPoints))
; and we want to start at a random starting position
; in the times tables
(dotimes [n (int (rand (inc (* cNumberPoints 2))))]
  (pointIndexWalker))

(defn drawCircle
  ; r - radius
  ; h, k - centre offset (h is x-offset, k is y-offset)
  ; rgb - map with keys :r, :g, :b for red, green and blue respectively
  ;     - sets the colour of the circle
  [ r h k rgb]
  (q/stroke (:r rgb) (:g rgb) (:b rgb))
  (q/fill (:r rgb) (:g rgb) (:b rgb))
  (let [ d (* 2 r) ] (q/ellipse h k d d)))


(defn getPoints
  ; generates n number of points
  ; on the circumference of the circle
  ; given by the radius and centre offset
  ; n - number of points
  ; r - radius
  ; h - x-offset from centre
  ; k - y-offset from centre
  [ n r h k]
  (let  [ tau q/TWO-PI
    ; startingPoint (* Math/PI 1.5) ;Math/PI
    ; startingPoint q/PI
    ; startingPoint (rand tau)
    startingPoint cStartingPoint
    ; I make PI the starting spot, so that the first point is on the left
    ; hand side on the horizontal centre of the circle
    angleList (iterate #(+ % (/ tau n)) startingPoint) ]
    ; (range startingPoint (* Math/PI 3)) ]
    (q/stroke 255 0 0)
    (q/fill 255 0 0)
    (q/stroke-weight 4)
    (q/text (str "startingPoint: " startingPoint) 10 20 100 100)
    (map (fn [ t ]
      (let [ x (+ (* r (q/cos t)) h) y (+ (* r (q/sin t)) k) ]
      ; (println  (str "(x, y): (" x ", " y ")"))
        [ x y ]
    )) (take n angleList))))



(defn drawPoints
  ; [ n r h k rgb ]
  [ points rgb ]
  (q/stroke (:r rgb) (:g rgb) (:b rgb))
  (q/fill (:r rgb) (:g rgb) (:b rgb))
  (q/stroke-weight 5)
  ; (def points (getPoints n r h k))
  (doseq [ [x y] points ]
    (q/point x y)))
        ; (q/ellipse x y 3 3)))

(defn drawLines
  [ points multiplier rgb ]
  (q/stroke (:r rgb) (:g rgb) (:b rgb))
  (q/stroke-weight 1)
  (let [c (count points)
    connections (map (fn [x] [x (mod (* x multiplier) c)]) (range 0 c))
    ]
    (doseq [ j connections ]
      (let [ i1 (get j 0) i2 (get j 1)
        p1 (get points i1) p2 (get points i2)
        ]
        (q/line p1 p2)))))

(defn drawRadii
  [ points centre rgb ]
  (q/stroke (:r rgb) (:g rgb) (:b rgb))
  (doseq [p points] (q/line p centre)))

(defn setup []
  ; (q/frame-rate 30)
  ; (q/frame-rate 0.5)
  ; (q/frame-rate 2)
  ; (q/frame-rate 4)
  (q/frame-rate 15)
)


(defn draw []
  ; (q/background 255)
  (q/background 0)
  (let [
    n cNumberPoints
    radius cRadius
    h (get sCentrePoint 0)
    k (get sCentrePoint 1)
    points (into [] (getPoints n radius h k))

    multiplier (pointIndexWalker)
    ; multiplier (int (rand n))
    mString (str "multiplier: " multiplier)
    ]

    (drawPoints points pointColour)
    ; (drawLines points 2 lineColour)
    ; (drawLines points 3 lineColour)
    ; (drawLines points 4 lineColour)
    ; (drawLines points 51 lineColour)
    ; 110 has love heart!
    (drawLines points multiplier lineColour)
    (q/stroke 255 0 0)
    (q/fill 255 0 0)
    (q/stroke-weight 4)
    (q/text mString 10 10 100 100)
))


(q/defsketch circle-sketch
  :title "Circles"
  :size [sWidth sHeight]
  :setup setup
  :draw draw
  :features [:keep-on-top])
