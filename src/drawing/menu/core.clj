(ns drawing.menu.core
  (:import    
    [javafx.application Application]
    [javafx.stage Stage]
    [javafx.scene Scene]
    [javafx.scene.control Button Label TextField PasswordField]
    [javafx.scene.layout GridPane]
    [javafx.event ActionEvent EventHandler]
    [javafx.geometry Pos Insets]
    [javafx.scene.text Font FontWeight Text])
  (:gen-class :main true
              :extends javafx.application.Application))

; (def sketches
;   (list
;     [0 0 "Fish Scale Pattrn"     drawing.circle.fish-scale-pattern]
;     [1 0 "Modulo Multiplication" drawing.circle.modulo-multiplication]
;     [2 0 "Koch Curve"            drawing.fractal.koch.curve]
;     [0 1 "Koch Snowflake"        drawing.fractal.koch.snowflake]
;     [1 1 "Sierpinski Arrowhead"  drawing.fractal.sierpinski.arrowhead]
;     [2 1 "Sierpinski Carpet"     drawing.fractal.sierpinski.carpet]
;     [0 2 "Sierpinski Play"       drawing.fractal.sierpinski.play]
;     [1 2 "Sierpinski Snowflake"  drawing.fractal.sierpinski.snowflake]
;     [2 2 "Sierpinski Triange"    drawing.fractal.sierpinski.triangle]
;     [0 3 "Wallis Seive"          drawing.fractal.wallis.seive]
;     [1 3 "Wallis Seive-Cross"    drawing.fractal.wallis.seive-cross]
;     [2 3 "Wallis Seive-Lattice"  drawing.fractal.wallis.seive-lattice]
;     [0 4 "Dragon Curve"          drawing.fractal.dragon]
;     [1 4 "Dragon Curve 2"        drawing.fractal.dragon2]
;     [2 4 "Dragon Curve 3"        drawing.fractal.dragon3]))

(defn -main [& args]
  (Application/launch drawing.menu.core args))


; (defn run-drawing [ns-symbol]
;   (require ns-symbol :reload-all)
;   (let [sketch (find-ns ns-symbol)]
;     ((ns-resolve sketch '-main))))

(defn run-drawing [ns-string]
  (let [ns-symbol (symbol ns-string)]
    (require ns-symbol :reload-all)    
    (let [sketch (find-ns ns-symbol)]
      ((ns-resolve sketch '-main)))))

(defn -start [this ^javafx.stage.Stage primaryStage]
  (let [
        fishScaleBtn (doto
                       (Button.)
                       (.setText "Fish Scale Pattern")
                       (.setOnAction
                         (proxy [EventHandler] []
                           (handle [event]
                             (run-drawing "drawing.circle.fish-scale-pattern")))))
        
        ; moduloBtn (doto
        ;             (Button.)
        ;             (.setText "Modulo Multiplication")
        ;             (.setOnAction
        ;               (proxy [EventHandler] []
        ;                 (handle [event]
        ;                   (run-drawing "drawing.circle.modulo-multiplication")))))
        
        
        grid (doto (GridPane.)
               (.setAlignment (Pos/CENTER))
               (.setHgap 10)
               (.setVgap 10)
               (.setPadding (Insets. 25 25 25 25))
               (.add fishScaleBtn 0 0))
        ; (.add (doto (Button.)                       
        ;         (.setText "Fishy")
        ;         (.setOnAction
        ;           (proxy [EventHandler] []
        ;             (handle [event]
        ;               (run-drawing "drawing.circle.fish-scale-pattern"))))) 0 0))
        
        ; (#(doseq [sketch sketches]
        ; (let [[col row text ns-symbol] sketch
        ; button  (doto (Button.)
        ;               (.setText text)
        ;               (.setOnAction
        ;                 (proxy [EventHandler] []
        ;                   (handle [event]
        ;                     (run-drawing ns-symbol)))))]
        ; (.add % button col row)))))
        
        scene (Scene. grid 600 600)]
    (doto primaryStage
      (.setTitle "Zsuark Quil Drawings")
      (.setScene scene)
      (.show))))
