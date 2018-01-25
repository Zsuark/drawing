# drawing

Software toys - simple drawings and play with Clojure and Quil. This repository is incomplete, and is supplementary to my blog. **If you are looking for perfection, please look elsewhere.**

This software is released "as is" with no guarantees whatsoever.

The code is not perfect, please don't expect it to be. The point is to have some working code to start with, and see how we can fix and improve it over time.

I will be discussing this software on [my blog - https://github.io/Zsuark](https://github.io/Zsuark). 

Please note: 

 - Some of the fractal curves are currently mislabeled.
 - There are three versions of the dragon curve, I will discuss why on my blog.

Also see:

 - The [Quil main website](http://quil.info/)
 - The [Quil GitHub repository](https://github.com/quil/quil)


## Usage

Use Leiningen to run the code.

`lein run` will run a javaFX-based menu for you to choose which quil sketch you would like to view.

### Options for lein run

 - `lein run`
 - `lein run -m drawing.circle.fish-scale-pattern`
 - `lein run -m drawing.circle.modulo-multiplication`
 - `lein run -m drawing.fractal.koch.curve`
 - `lein run -m drawing.fractal.koch.snowflake`
 - `lein run -m drawing.fractal.sierpinski.arrowhead`
 - `lein run -m drawing.fractal.sierpinski.carpet`
 - `lein run -m drawing.fractal.sierpinski.play`
 - `lein run -m drawing.fractal.sierpinski.snowflake`
 - `lein run -m drawing.fractal.sierpinski.triangle`
 - `lein run -m drawing.fractal.wallis.seive`
 - `lein run -m drawing.fractal.wallis.seive-cross`
 - `lein run -m drawing.fractal.wallis.seive-lattice`
 - `lein run -m drawing.fractal.dragon`
 - `lein run -m drawing.fractal.dragon2`
 - `lein run -m drawing.fractal.dragon3`

You may also require and run the sketches from a REPL.

## Some Known Problems

- Using the quit keyboard-shortcut (e.g. cmd-Q on Mac) on a sketch window generates a JVM runtime error
  - Similar error when you quit on the menu window with a sketch open
- Setting a title in a sketch has no effect. The sketch window is always titled "Applet".
- Code needs to be reviewed and blogged about
- Some fractals are named incorrectly

## License

Copyright © 2018 Raphael Krausz

 - Find me on [GitHub http://github.com/Zsuark](http://github.com/Zsuark)
 - Find me on [BitBucket http://bitbucket.com/Zsuark](http://bitbucket.com/Zsuark)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
