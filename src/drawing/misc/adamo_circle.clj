(ns drawing.misc.adamo-circle
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m])
  (:gen-class))

(def sWidth  700)
(def sHeight 700)
; Try different widths (by radius increments)

; (def sWidth  150)
; (def sHeight 150)

(def sWidth  600)
(def sHeight 600)

(def radius 25)
(def diameter (* 2 radius))

; http://flatuicolors.com/
;   My choices:
;     - red    #e74c3c
;     - yellow #f1c40f
;     - orange #e67e22
;     - blue   #3498db
;     - green  #2ecc71

#_ "RGB colour is alpha, red, green, blue"
#_ "Change the alpha from ff to aa for an interesting effect
    Leave the white at ff also"
(def colours
  {:red    0xffe74c3c
   :yellow 0xfff1c40f
   :orange 0xffe67e22
   :blue   0xff3498db
   :green  0xff2ecc71
   :white  0xffffffff})


(def fps        30)
(def framePause  5)
(def secondsPause (* fps framePause))


; 700/25 = 28. So, the screen is 28 radii across or 14 diameters across
; Probably good to note this for later.

(defn
  get-next-colour
  "Circular procession of colours"
  [colour]
  (case colour
    :blue   :yellow    
    :yellow :red
    :red    :orange
    :orange :green 
    :green  :blue))

#_ "NB: Circles are made up of an x y coordinates and a colour"


(defn
  setup-state
  "Sets up empty state ready for first iteration"
  []
  {:circles     (list)
   :last-circle [ (- diameter) 0 :orange ]
   :pause       secondsPause})


(defn
  setup
  []
  
  #_ "Set up frame rate, stroke colour and weight"
  (q/frame-rate fps)
  (q/stroke (:white colours))
  (q/stroke-weight 10)
  
  #_ "Kick off initial state"
  (setup-state))



(defn
  draw-state
  [state]
  (q/background (:white colours))
  (doseq
    [[x y c] (:circles state)]
    (let
      [colour (get colours c)]
      (q/fill colour)
      (q/ellipse x y diameter diameter))))

(defn
  update-coordinates
  "Given a set of x y coordinates, return the next x y coordinates in the set"
  [x y]
  (let [
        x1          (+ x diameter)
        boundary    (+ sWidth radius)
        outOfBounds (> x1 boundary)
        newX        (if outOfBounds
                      (if (zero? (mod y 2))
                        radius
                        0)
                      x1)
        newY        (if outOfBounds
                      (+ y radius)
                      y)]
    (vector newX newY)))

(defn
  update-state
  "Given a state, return the next iteration of the state"
  [state]
  (let [
        [ last-x last-y last-colour ] (:last-circle state)
        [ new-x new-y ]               (update-coordinates last-x last-y)
        new-colour                    (get-next-colour last-colour)
        new-circle                    (vector new-x new-y new-colour)
        new-circles                   (concat (:circles state) (list new-circle))]
    
    #_ "Pause and reset running if the screen is full"
    (if (>= new-y (+ sHeight diameter))
      (let [pause (:pause state)]
        (if (> pause 0)
          (update-in state [:pause] dec)
          (assoc-in (setup-state) [:last-circle 2] new-colour)))
      
      #_ "Otherwise dreturn an updated version of the state"
      {:circles     new-circles
       :last-circle new-circle
       :pause       (:pause state)})))


(defn -main
  []
  (q/sketch
    :title  "Adamo Circles"
    :host   "adamo"
    :size   [sWidth sHeight]
    :setup  setup
    :update update-state
    :draw   draw-state
    :middleware [m/fun-mode]))

