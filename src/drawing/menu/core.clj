(ns drawing.menu.core
  (:import
    [javafx.application Application]
    [javafx.scene Scene]
    [javafx.scene.control Button]
    [javafx.scene.layout GridPane]
    [javafx.event EventHandler]
    [javafx.geometry Pos Insets])
  (:gen-class :main true
              :extends javafx.application.Application))


(def sketches
  (list
    [0 0 "Fish Scale Pattern"    "drawing.circle.fish-scale-pattern"]
    [1 0 "Modulo Multiplication" "drawing.circle.modulo-multiplication"]
    [2 0 "Koch Curve"            "drawing.fractal.koch.curve"]
    [0 1 "Koch Snowflake"        "drawing.fractal.koch.snowflake"]
    [1 1 "Sierpinski Arrowhead"  "drawing.fractal.sierpinski.arrowhead"]
    [2 1 "Sierpinski Carpet"     "drawing.fractal.sierpinski.carpet"]
    [0 2 "Sierpinski Play"       "drawing.fractal.sierpinski.play"]
    [1 2 "Sierpinski Snowflake"  "drawing.fractal.sierpinski.snowflake"]
    [2 2 "Sierpinski Triange"    "drawing.fractal.sierpinski.triangle"]
    [0 3 "Wallis Seive"          "drawing.fractal.wallis.seive"]
    [1 3 "Wallis Seive-Cross"    "drawing.fractal.wallis.seive-cross"]
    [2 3 "Wallis Seive-Lattice"  "drawing.fractal.wallis.seive-lattice"]
    [0 4 "Dragon Curve"          "drawing.fractal.dragon"]
    [1 4 "Dragon Curve 2"        "drawing.fractal.dragon2"]
    [2 4 "Dragon Curve 3"        "drawing.fractal.dragon3"]))

(defn -main [& args]
  (Application/launch drawing.menu.core args))

(defn run-drawing [ns-string]
  (let [ns-symbol (symbol ns-string)]
    (require ns-symbol :reload-all)
    ((ns-resolve (find-ns ns-symbol) '-main))))

(defn -start [this ^javafx.stage.Stage primaryStage]
  (let [grid (doto (GridPane.)
               (.setAlignment Pos/CENTER)
               (.setHgap 20)
               (.setVgap 15)
               (.setPadding (Insets. 25 25 25 25))
               (#(doseq [sketch sketches]
                   (let [[col row text ns-string] sketch
                         button  (doto (Button.)
                                   (.setText text)
                                   (.setOnAction
                                     (proxy [EventHandler] []
                                       (handle [event]
                                         (run-drawing ns-string)))))]
                     (.add % button col row)))))
        scene (Scene. grid 550 250)]
    (doto primaryStage
      (.setTitle "Zsuark Quil Drawings")
      (.setScene scene)
      (.show))))
