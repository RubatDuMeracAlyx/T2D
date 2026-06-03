// Scala CLI config so the game can be built/run without IntelliJ.
//   scala run .        -> launches the Menu (game entry point)
//   scala compile .    -> just type-check
// The toolchain's default Scala is 3.x, so the 2.13 pin below is required.
//> using scala 2.13.16
//> using jar lib/gdx2d-desktop-1.3.0.jar lib/accordion-1.2.0-jar-with-dependencies.jar
//> using resourceDir data
//> using mainClass ch.hevs.gdx2d.mygame.Menu
