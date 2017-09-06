(ns drawing.lines
	(:require [quil.core :as q]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb)
  (q/stroke 125 255 125))


(defn draw []
	(q/ellipse 100 100 200 200)
  (q/line 0 0 (q/mouse-x) (q/mouse-y))
  (q/line 200 0 (q/mouse-x) (q/mouse-y))
  (q/line 0 200 (q/mouse-x) (q/mouse-y))
  (q/line 200 200 (q/mouse-x) (q/mouse-y)))

(q/defsketch hello-lines
  :title "You can see lines"
  :size [500 500]
  :setup setup
  :draw draw
  :features [:keep-on-top])
