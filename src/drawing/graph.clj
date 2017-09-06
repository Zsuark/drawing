(ns drawing.graph
	(:require [quil.core :as q :include-macros true]
  ))

(use '[drawing.fibonacci :only [countFibStack]])

(defn drawAxes []
  (q/stroke 125 75 75)
  (q/stroke-weight 10)
  (q/line [0 0] [500 0])
  (q/line [0 0] [0 500]))

(defn drawCentre []
  (q/stroke 255 75 75)
  (q/fill 255 75 75)
  (q/stroke-weight 35)
  (q/point 500 400))

(defn setup []
  (drawAxes)
  (q/stroke 255 125 75)
  (q/line [0 0] [1000 1000])
  (q/stroke 75 125 255)
  (q/line [1000 1000] [0 0])
  (q/line [0 1000] [0 0])
  (q/line [1000 0] [0 0])
  (q/line [0 0] [0 1000])
  (q/line [0 0] [1000 0])
  (drawCentre)
)

(defn draw []
  (q/stroke 125 255 125)
  (q/fill 75 255 125)
  (q/stroke-weight 1)
  (println "type q/width" (type q/width))
	; (q/ellipse 400 400 (/ q/width 2) (/ q/height 2))
)

(q/defsketch hello-lines
  :title "Graphing"
  :size [1000 800]
  :setup setup
  :draw draw
  :features [:keep-on-top])
