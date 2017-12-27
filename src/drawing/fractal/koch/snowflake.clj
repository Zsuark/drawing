(ns drawing.fractal.koch.snowflake
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))


(comment "

         drawing.koch Progressively creates a Koch Snowflake on screen, and repeats.

         We are writing this in Clojure and using Quil to draw.

         Helge von Koch was a Swedish mathematician concerned with number theory. His achievements
         were incredible, but we will look at one of his smaller achievements. Von Koch described
         a fractal now known as the Koch Snoflake and Koch Curve.

         The Koch Curve is a repeated line pattern, where a point on a line is formed from a pattern
         of lines that match the line the original point belongs to. Best you read up on this so you
         understand what I mean.

         The Koch Curve is formed by taking a line, dividing it into three,
         and replacing the middle segment with an equilateral triangle missing it's base.


         so: ___ -> _ _ _ -> _ / \\ _ -> _/\\_

         Each of those line segments then gets the process applied to them.

         When the base lines given are arranged into a triangle, you get a realistic looking
         snowflake outline.


         These things formed from these processes, where the line segment is self-similar are called
         fractals. Fractals are part of geometry and found naturally occuring in nature.

         Understanding geometry (both synthetic and analytic) is an important part of the mathematics
         used in Computer Science and our understanding of the world.

         Playing with fun smaller graphical projects helps us enjoy learning them. Their application
         however is for more than just games. The concepts used are have near universal application.


         https://en.wikipedia.org/wiki/Helge_von_Koch
         https://en.wikipedia.org/wiki/Koch_snowflake
         https://en.wikipedia.org/wiki/Fractal
         http://ecademy.agnesscott.edu/~lriddle/ifs/kcurve/kcurve.htm
         http://www.tgmdev.be/applications/acheron/curves/curvevonkoch.php
         http://www.math.ubc.ca/~cass/courses/m308/projects/fung/page.html

         ")

; https://clojure.org/reference/datatypes


#_ "TODO: These elements should be configurable by the webpage"
; (def sWidth  800)
; (def sHeight 800)
(def sWidth  320)
(def sHeight 357)

; How many frames to pause at end of animation before restarting
(def pauseFrames 5)

#_ "
We are using functions that all work upon points and lines.

A line segment on the cartesian plane has a start and an end point.
Each point has is formed from a pair of (x,y) coordinates.

(When we talk about lines, we are talking about straight line segments.)

Note that as we indicate a start and end point, we give the line direction, this direction
allows us to draw the Koch Curve on the correct side of each line.

1 line - 2 points
1 point - 2 coordinates

So, as I'm learning clojure, I'm going to encapsulate the line and point as types.
"

(deftype Point [x y])
(deftype Line [start end])


(defn makePoint
  "Added in to help with CLJS headaches"
  [x y]
  (Point. x y))

(defn makeLine
  "Added in to help with CLJS headaches"
  [start end]
  (Line. start end))


(defn dx
  "distance of x coordinates between the two points of a line"
  [line]
  (let [start (.-start line)
        end   (.-end line)
        x1    (.-x start)
        x2    (.-x end)]
    (- x2 x1)))

(defn dy
  "distance of y coordinates between the two points of a line"
  [line]
  (let [start (.-start line)
        end   (.-end line)
        y1    (.-y start)
        y2    (.-y end)]
    (- y2 y1)))

(defn distance
  "Find the distance of a given line"
  [line]
  (let [dx (dx line)
        dy (dy line)
        ]
    (q/sqrt (+ (* dx dx) (* dy dy)))))


(defn divideLine
  "Takes a line, and divides it into 3 segments
  distance x (dx): x2 - x1
  distance y (dy): y2 - y1

  Midpoint:
  M = x: (x1 + x2) / 2, y: (y1 + y2) / 2

  Therefore,

  1/3 point:
  OneThirdPoint = x: (x1 + x2) / 3, y: (y1 + y2) / 3

  2/3 point:
  TwoThirdPoint = x: 2 * OneThirdPoint.x, y: 2 * OneThirdPoint.y

  Note that the above calculations assumes x1,y1 is the origin, so
  the actual results will need to be offset by the the start point.
  "
  [line]
  (let [dx (dx line)
        dy (dy line)
        startPoint    (.-start line)
        endPoint      (.-end   line)
        x1            (.-x startPoint)
        y1            (.-y startPoint)
        one3rdDx      (/ dx 3)
        one3rdDy      (/ dy 3)
        x2            (+ x1 one3rdDx)
        y2            (+ y1 one3rdDy)
        one3rdPoint   (Point. x2 y2)
        x3            (+ x1 (* 2 one3rdDx))
        y3            (+ y1 (* 2 one3rdDy))
        twoThirdPoint (Point. x3 y3)]
    (vector
      (Line. startPoint one3rdPoint)
      (Line. one3rdPoint twoThirdPoint)
      (Line. twoThirdPoint endPoint))))

#_ "
I want to take a line and convert it into a equilateral triangle,
with the base line removed

Cartesian geometry hints:
https://math.stackexchange.com/questions/9365/endpoint-of-a-line-knowing-slope-start-and-distance

slope - m - is rise over run - dy / dx
slope angle is arctan(m)

We know the length of the lines to be used (all the same as the line given)
However, we don't know x and y differences.

slope is also generated by: m = tan(angle)

The new lines will be the same length as the original lines, but the
first's end and the second's start will be on the midpoint of the given line.

However, we don't know the y positing of the first new line's end point, or the
second's start point.

In effect we know the length of an imaginary adjacent, the hypoteneuse, and the angle
(60 degrees or Pi/3 radians)

a^2 + b^2 = c^2

b^2 = c^2 - a^2

b = sqrt( c^2 - a^2 )

a = dx/2
c = length of original line

---

c^2 = a^2 + b^2

theta1: angle of line against horizon (x-axis)
theta2: angle of new line against horizon - theta1 + PI/3 radians
"

(defn makeTriangle
  "Replaces line with a list of two new lines,
  formed from making an equilateral triange with the given line as base,
  and but with it's base line removed"
  [line]
  (let [
        startPoint  (.-start line)
        endPoint    (.-end   line)
        x1          (.-x  startPoint)
        y1          (.-y  startPoint)
        dx          (dx line)
        dy          (dy line)
        d           (distance line)
        theta1      (q/atan2 dy dx)
        theta2      (- theta1 (/ q/PI 3))
        x2          (+ (* d (q/cos theta2)) x1)
        y2          (+ (* d (q/sin theta2)) y1)
        ]
    (vector
      (Line. startPoint (Point. x2 y2))
      (Line. (Point. x2 y2) endPoint))))


(defn update-line
  "Given a line, returns a vector of four lines
  - representing the line with the koch algorithm applied to it"
  [line]
  (let [[firstLine middleLine lastLine] (divideLine line)
        [secondLine thirdLine] (makeTriangle middleLine)]
    (vector firstLine secondLine thirdLine lastLine)))


#_ "Not strictly needed, but helpful when testing"
(defn reverseLine
  "Given a line from x1y1 to x2y2, give back a line from x2y2 to x1y1"
  [line]
  (let [originStart (.-start line)
        originEnd   (.-end   line)
        newLine     (Line. originEnd originStart)]
    newLine))


(defn setup-triangle
  "Set a hashmap of lines to a list of lines of first triangle"
  []
  (let [
        x1  10
        ;y1  220
        y1  92
        ; x2  692
        x2 310
        ;x2 253
        y2  y1
        side1 (Line. (Point. x1 y1) (Point. x2 y2))
        backLine   (reverseLine side1)
        [side2 side3] (makeTriangle backLine)]
    {:lines (list side1 side2 side3)}))


(defn setup
  "set frame rate and setup state"
  []
  (q/frame-rate 0.6)
  (assoc (setup-triangle) :pause pauseFrames))


(defn update-state
  "Given a state, generation the next iteration"
  [state]
  (let [firstLine (first (:lines state))
        length  (distance firstLine)]
    #_ "If the length of the first line is less than 3, it means that it is
       now difficult to display any further line details.

       So, lets continue if line length is greater than 3, otherwise
       let's reset and start again."
       (if (>= length 3)
         (do
           ; (println "continuing to apply - line length: " length)
           (assoc state :lines (mapcat update-line (:lines state))))
         (if (> (:pause state) 0)
           (do
             ; (println "pausing - " (:pause state) ", length: " length)
             (update state :pause dec))
           (do
             ; (println "Continuing - pause value: " (:pause state))
             (assoc (setup-triangle) :pause pauseFrames))))))


(defn draw-state
  "Redraw the screen according to "
  [state]
  ; (q/background 0)
  ; (q/stroke 0 255 255)
  ; (q/fill 0 255 255)

  ; (q/background 255)
  ; (q/stroke 0 125 255)
  ; (q/fill 0 125 255)
  
  (q/background 0 105 255)
  (q/stroke     255)
  (q/fill       255)

  (q/stroke-weight 2)

  ; Update to stdout - commented out for now.
  ; (println "-----\nThere are" (count (:lines state)) "lines")

  (doseq [line (:lines state)
          :let [start (.-start line)
                end   (.-end line)
                x1    (.-x start)
                y1    (.-y start)
                x2    (.-x end)
                y2    (.-y end)]]
    (q/line x1 y1 x2 y2)))


(defn run [host]
  (q/sketch
    :host host
    :title "Koch Snowflake"
    :size [sWidth sHeight]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))


(defn -main [] (run "koch"))

; Old defsketch commented out
#_ (q/defsketch koch-sketch
                :title "Koch Snowflake"
                :size [sWidth sHeight]
                :setup setup
                :update update-state
                :draw draw-state
                :middleware [m/fun-mode])
