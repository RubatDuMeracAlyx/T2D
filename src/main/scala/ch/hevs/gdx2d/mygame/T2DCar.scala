package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.physics.primitives.PhysicsBox
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.math.Vector2

class T2DCar(var position: Vector2){
  var accelerate: Boolean = false

  val box = new PhysicsBox("car", position, 150f, 45f, math.toRadians(90.0).toFloat)
  private val carImage = new BitmapImage("data/res/CARS/REDCAR/redCar.png")
  var driveUp = 0f
  var driftLeft = false
  var driftRight = false

  def draw(g: GdxGraphics): Unit = {
    //DRIFT LEFT AND RIGHT //has to diminish with the speed -> max is when the car goes full speed and you have to be unable to turn when speed is 0

    //calculate speed of the car
    println(box.getBodyLinearVelocity)


    if (driftLeft) box.applyBodyTorque(T2DCar.MAX_TORQUE, true)
    if (driftRight) box.applyBodyTorque(-T2DCar.MAX_TORQUE, true)

    //goes forward with the angle the car is at

    //calculate ratio beetween the two thing
    box.applyBodyForceToCenter(
      math.cos(box.getBodyAngle.toDouble).toFloat * driveUp-T2DCar.DRAG,
      math.sin(box.getBodyAngle.toDouble).toFloat * driveUp-T2DCar.DRAG,
      true)

    //position of the car
    val pos = box.getBodyPosition

    //apply the speed
    g.drawTransformedPicture(pos.x, pos.y, box.getBodyAngleDeg, .5f, carImage)
  }
}

object T2DCar {
  val MAX_THRUST = 5f
  val MAX_TORQUE = 1f
  val DRAG = 1f
}


