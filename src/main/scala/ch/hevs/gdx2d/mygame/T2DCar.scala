package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.physics.primitives.PhysicsBox
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.math.Vector2

class T2DCar(position: Vector2, angle: Float){
  var accelerate: Boolean = false

  private val box = new PhysicsBox("car", position, 30f, 30f, math.toRadians(90.0).toFloat)
  private val shipImage = new BitmapImage("res/car_bin.png")
  var thrustUp = 0f

  override def draw(g: GdxGraphics): Unit = {
    if (thrustLeft) box.applyBodyTorque(Spaceship.MAX_TORQUE, true)
    if (thrustRight) box.applyBodyTorque(-Spaceship.MAX_TORQUE, true)

    box.applyBodyForceToCenter(
      math.cos(box.getBodyAngle.toDouble).toFloat * thrustUp,
      math.sin(box.getBodyAngle.toDouble).toFloat * thrustUp,
      true)

    val pos = box.getBodyPosition

    if (thrustUp > 0) {
      val x = box.getBody.getWorldPoint(new Vector2(-55.0f * 0.02f, 0.0f)) // simplified world point calc
      // The original code had a weird calc: val x = box.body.getWorldPoint(Vector2(-55.0f, 0.0f)); val flameCenter = x.add(pos)
      // Actually getWorldPoint already returns world coordinates.
      val flameCenter = box.getBody.getWorldPoint(new Vector2(-30f / 2f / 50f, 0f)) // Approximation for the back
      // Using a simpler one:
      g.drawTransformedPicture(pos.x, pos.y, box.getBodyAngleDeg, .3f, flameImage)
    }

    g.drawTransformedPicture(pos.x, pos.y, box.getBodyAngleDeg, .5f, shipImage)
  }
}

object Spaceship {
  val MAX_THRUST = 1f
  val MAX_TORQUE = 0.04f
}

}
