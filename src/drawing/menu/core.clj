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

(defn add-menu-location
  "
  Prepends each sketch [name ns-name] with column and row information.
  Assumes a 3 column arangement.
  "
  [sketches]
  (let [c (count sketches)
        row-count (+
                    (/ c 3)
                    (if (zero? (mod c 3))
                      0
                      1))]
    (map concat (for [row (range row-count) col (range 3)] [col row]) sketches)))

(def sketches
  (list
    ["Cantor Dust"           "drawing.fractal.cantor.dust"]
    ["Dragon Curve"          "drawing.fractal.dragon3"]
    ["Fish Scale Pattern"    "drawing.circle.fish-scale-pattern"]
    ["Koch Curve"            "drawing.fractal.koch.curve"]
    ["Koch Snowflake"        "drawing.fractal.koch.snowflake"]
    ["Modulo Multiplication" "drawing.circle.modulo-multiplication"]
    ["Sierpinski Arrowhead"  "drawing.fractal.sierpinski.arrowhead"]
    ["Sierpinski Carpet"     "drawing.fractal.sierpinski.carpet"]
    ["Sierpinski Triangle"   "drawing.fractal.sierpinski.triangle"]
    ["Vicsek Fractal"        "drawing.fractal.vicsek"]
    ["Wallis Seive"          "drawing.fractal.wallis.seive"]
    ["Play Snake!"           "drawing.snake.core"]
))

(defn -main [& args]
  (Application/launch drawing.menu.core args))

(defn run-drawing [ns-string]
  (let [ns-symbol (symbol ns-string)]
    (require ns-symbol :reload-all)
    ((ns-resolve (find-ns ns-symbol) '-main))))

(defn -start [this ^javafx.stage.Stage primaryStage]
  (let [indexed-sketches (add-menu-location sketches)
        grid (doto (GridPane.)
               (.setAlignment Pos/CENTER)
               (.setHgap 20)
               (.setVgap 15)
               (.setPadding (Insets. 25 25 25 25))
               (#(doseq [sketch indexed-sketches]
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
