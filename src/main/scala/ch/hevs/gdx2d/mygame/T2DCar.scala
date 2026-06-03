package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.physics.primitives.PhysicsBox
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.mygame.T2DCar.{BoostValue, MAX_THRUST, MAX_TORQUE}
import com.badlogic.gdx.math.Vector2

import scala.math.BigDecimal.double2bigDecimal

class T2DCar(var position: Vector2){
  var accelerate: Boolean = false

  val box = new PhysicsBox("car", position, 150f, 45f, math.toRadians(90.0).toFloat)
  private val carImage = new BitmapImage("data/res/CARS/BLUECAR/blueCar.png")
  var driveUp = 0f
  var driftLeft = false
  var driftRight = false
  var driveDown = 0f
  var boost = false

  def draw(g: GdxGraphics): Unit = {
    //DRIFT LEFT AND RIGHT //has to diminish with the speed -> max is when the car goes full speed and you have to be unable to turn when speed is 0
    //calculate speed of the car
    var speed = getDistanceVector(box)
    if(boost){
      box.applyBodyForceToCenter(
        math.cos(box.getBodyAngle.toDouble).toFloat * BoostValue,
        math.sin(box.getBodyAngle.toDouble).toFloat * BoostValue,
        true)
    }

    if (driftLeft)box.applyBodyTorque(T2DCar.MAX_TORQUE*speed, true)
    if (driftRight)box.applyBodyTorque(-T2DCar.MAX_TORQUE*speed, true)

    if(box.getBodyAngularVelocity > 0)box.applyBodyTorque(-T2DCar.DRAG_TORQUE, true)
    if(box.getBodyAngularVelocity < 0)box.applyBodyTorque(T2DCar.DRAG_TORQUE, true)



    box.applyBodyForceToCenter(
      -math.cos(box.getBodyAngle.toDouble).toFloat * driveDown,
      -math.sin(box.getBodyAngle.toDouble).toFloat * driveDown,
      true
    )



    //goes forward with the angle the car is at

    //calculate ratio beetween the two thing

    box.applyBodyForceToCenter(
      math.cos(box.getBodyAngle.toDouble).toFloat * driveUp,
      math.sin(box.getBodyAngle.toDouble).toFloat * driveUp,
      true)

    box.applyBodyForceToCenter(
      box.getBodyLinearVelocity.x * -T2DCar.DRAG_THRUST * speed,
      box.getBodyLinearVelocity.y * -T2DCar.DRAG_THRUST * speed,
      true)

    //position of the car
    val pos = box.getBodyPosition
    //apply the speed
    g.drawTransformedPicture(pos.x, pos.y, box.getBodyAngleDeg, .5f, carImage)
  }
  def getDistanceVector(box:PhysicsBox):Float={
    return (Math.sqrt(Math.pow(box.getBodyLinearVelocity.x,2)+Math.pow(box.getBodyLinearVelocity.y,2)).toFloat)
  }
}



object T2DCar {
  var MAX_THRUST = 20f
  var MAX_TORQUE = 0.5f
  val DRAG_THRUST = 0.5f
  val DRAG_TORQUE = 1.3f
  val BoostValue = 105f

}


