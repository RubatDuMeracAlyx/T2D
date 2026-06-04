package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.physics.primitives.PhysicsBox
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.AbstractPhysicsObject
import ch.hevs.gdx2d.mygame.Car.{BoostValue, DRAG_THRUST, DRAG_TORQUE, MAX_TORQUE}
import com.badlogic.gdx.math.Vector2

import scala.collection.mutable.ArrayBuffer

class Player(var position: Vector2, nCheckpoints: Int) extends PhysicsBox("car", position, 150f, 45f, math.toRadians(90.0).toFloat){
  var accelerate: Boolean = false

  private val carImage = new BitmapImage("data/res/CARS/BLUECAR/blueCar.png")
  var driveUp = 0f
  var driftLeft = false
  var driftRight = false
  var driveDown = 0f
  var boost = false
  var speed = 0f
  var onSand = false
  var stateOfTheCheckpoint : ArrayBuffer[Boolean] = ArrayBuffer.empty
  var nDrivenLapsInClass : Int = 0
  var finished : Boolean = false

  createTheStateOfTheCheckpoint(nCheckpoints)

  def didIWentThoughAllTheCheckpoints (checkpointState: ArrayBuffer[Boolean]) : Boolean = {
    var result : Boolean = true

    for (c <- checkpointState){
      if (c == false){return false}
    }

    result
  }

  def logicForTheFinishBloc(checkpointState: ArrayBuffer[Boolean], nDrivenLapsInFunc: Int): Unit = {
    if (didIWentThoughAllTheCheckpoints(checkpointState) == true && nDrivenLapsInClass == 2){
      finished = true
      println("FINISHED!")
    }
    else if (didIWentThoughAllTheCheckpoints(checkpointState) == true){
      nDrivenLapsInClass += 1
      for (i <- checkpointState.indices){
        checkpointState(i) = false
      }
      println(checkpointState)
    }
  }

  def createTheStateOfTheCheckpoint (nCheckpoints : Int) : Unit= {
    for (i <- 0 until nCheckpoints){
      stateOfTheCheckpoint.addOne(false)
    }
  }
  
  def draw(g: GdxGraphics): Unit = {
    speed = getDistanceVector(this)
    //booster
    if(boost){
      this.applyBodyForceToCenter(
        math.cos(this.getBodyAngle.toDouble).toFloat * BoostValue,
        math.sin(this.getBodyAngle.toDouble).toFloat * BoostValue,
        true)
    }

    //turns the car
    if (driftLeft)this.applyBodyTorque(MAX_TORQUE*speed, true)
    if (driftRight)this.applyBodyTorque(-MAX_TORQUE*speed, true)
    //stop the inertia of the turn
    if(this.getBodyAngularVelocity > 0)this.applyBodyTorque(-DRAG_TORQUE, true)
    if(this.getBodyAngularVelocity < 0)this.applyBodyTorque(DRAG_TORQUE, true)

    //goes backward
    this.applyBodyForceToCenter(
      -math.cos(this.getBodyAngle.toDouble).toFloat * driveDown,
      -math.sin(this.getBodyAngle.toDouble).toFloat * driveDown,
      true)
    //goes forward
    this.applyBodyForceToCenter(
      math.cos(this.getBodyAngle.toDouble).toFloat * driveUp,
      math.sin(this.getBodyAngle.toDouble).toFloat * driveUp,
      true)
    //stops the inertia of the car
    this.applyBodyForceToCenter(
      this.getBodyLinearVelocity.x * -DRAG_THRUST * speed,
      this.getBodyLinearVelocity.y * -DRAG_THRUST * speed,
      true)

    //position of the car
    val pos = this.getBodyPosition
    //apply the speed
    g.drawTransformedPicture(pos.x, pos.y, this.getBodyAngleDeg, .5f, carImage)
  }
  def getDistanceVector(box:PhysicsBox):Float={
    return (Math.sqrt(Math.pow(box.getBodyLinearVelocity.x,2)+Math.pow(box.getBodyLinearVelocity.y,2)).toFloat)
  }
}



object Car {
  var MAX_THRUST = 20f
  var MAX_TORQUE = 0.5f
  val DRAG_THRUST = 0.5f
  val DRAG_TORQUE = 1.3f
  val BoostValue = 105f

}


