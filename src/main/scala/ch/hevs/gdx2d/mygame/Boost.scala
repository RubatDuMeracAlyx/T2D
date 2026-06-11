package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.physics.primitives.PhysicsBox
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.math.Vector2

class Boost (var boostNbr : Int ,var position: Vector2) extends PhysicsBox (boostNbr.toString, position, 128f, 128f, 0f){

  var amount : Int = (math.random() * 50).toInt + 10
  var imageNum : Int = if (amount <= 20){1}else if (amount <= 40){2} else {3} // change the skin of the boost based on the amount of fuel given
  val boostImage = new BitmapImage("data/res/BOOST/Boost" + imageNum + ".png")
  val pos: Vector2 = this.getBodyPosition
  var undraw : Boolean = false


  def draw (g: GdxGraphics): Unit = { // Func to draw the boost on the map
    g.drawAlphaPicture(pos.x, pos.y, this.getBodyAngleDeg, 0.5f, 1f, boostImage)
  }
}
