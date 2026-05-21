package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.physics.primitives.PhysicsBox
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.math.Vector2

class T2DCar(position: Vector2){
  var accelerate: Boolean = false

  private val box = new PhysicsBox("car", position, 30f, 30f, math.toRadians(90.0).toFloat)
  private val carImage = new BitmapImage("src/main/scala/ch/hevs/gdx2d/mygame/res/car_bin.png")
  var driveUp = 0f
  var driftLeft = false
  var driftRight = false

  def draw(g: GdxGraphics): Unit = {
    if (driftLeft) box.applyBodyTorque(T2DCar.MAX_TORQUE, true)
    if (driftRight) box.applyBodyTorque(-T2DCar.MAX_TORQUE, true)

    box.applyBodyForceToCenter(
      math.cos(box.getBodyAngle.toDouble).toFloat * driveUp,
      math.sin(box.getBodyAngle.toDouble).toFloat * driveUp,
      true)

    val pos = box.getBodyPosition

    if (driveUp > 0) {
      val x = box.getBody.getWorldPoint(new Vector2(-55.0f * 0.02f, 0.0f)) // simplified world point calc
      g.drawTransformedPicture(pos.x, pos.y, box.getBodyAngleDeg, .3f, carImage)
    }

    g.drawTransformedPicture(pos.x, pos.y, box.getBodyAngleDeg, .5f, carImage)
  }
}

object T2DCar {
  val MAX_THRUST = 1f
  val MAX_TORQUE = 0.04f
}


