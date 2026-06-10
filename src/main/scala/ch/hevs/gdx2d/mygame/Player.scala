package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.physics.primitives.PhysicsBox
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.AbstractPhysicsObject
import ch.hevs.gdx2d.mygame.Car.{BoostValue, DRAG_THRUST, DRAG_TORQUE, MAX_TORQUE}
import com.badlogic.gdx.math.Vector2

import scala.collection.mutable.ArrayBuffer

class Player(var playerNbr : Int ,var position: Vector2, nCheckpoints: Int) extends PhysicsBox(playerNbr.toString, position, 150f, 45f, math.toRadians(90.0).toFloat){
  var accelerate: Boolean = false
  val carImage = new BitmapImage("data/res/CARS/BLUECAR/blueCar" + (playerNbr + 1) + ".png")
  var driveUp = 0f
  var driftLeft = false
  var driftRight = false
  var driveDown = 0f
  var boost = false
  var speed = 0f
  var stateOfTheCheckpoint : ArrayBuffer[Boolean] = ArrayBuffer.empty
  var nDrivenLapsInClass : Int = 0
  var finished : Boolean = false
  var onSand = false
  val sandReductionFactor = 0.2f
  var onGrass = false
  val grassReductionFactor = 0.1f
  var pos:Vector2 = _

  createTheStateOfTheCheckpoint(nCheckpoints)

  // TODO def everyoneFinished (arrayBuffer: ArrayBuffer[Player]) : Unit = {}

  def wentThoughAllCP (checkpointState: ArrayBuffer[Boolean]) : Boolean = {
    var result : Boolean = true

    for (c <- checkpointState){
      if (c == false){return false}
    }

    result
  }

  def logicForTheFinishBloc(checkpointState: ArrayBuffer[Boolean], nDrivenLapsInFunc: Int): Unit = {
    if (wentThoughAllCP(checkpointState) == true && nDrivenLapsInClass == 3){ // checking if the player went though every CP and if he did 3 laps
      finished = true
      println("FINISHED!")
    }
    else if (wentThoughAllCP(checkpointState) == true){ // checking if the player went though every CP
      nDrivenLapsInClass += 1
      for (i <- checkpointState.indices){
        checkpointState(i) = false
      }
      println("lap " + nDrivenLapsInClass + " / 3")
    }
  }

  def createTheStateOfTheCheckpoint (nCheckpoints : Int) : Unit= {
    for (i <- 0 until nCheckpoints){
      stateOfTheCheckpoint.append(false)
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
    if(onSand){
      this.applyBodyForceToCenter(
        -math.cos(this.getBodyAngle.toDouble).toFloat * driveDown * sandReductionFactor,
        -math.sin(this.getBodyAngle.toDouble).toFloat * driveDown * sandReductionFactor,
        true)
    }
    else if (onGrass){
      this.applyBodyForceToCenter(
        -math.cos(this.getBodyAngle.toDouble).toFloat * driveDown * grassReductionFactor,
        -math.sin(this.getBodyAngle.toDouble).toFloat * driveDown * grassReductionFactor,
        true)
    }
    else {
      this.applyBodyForceToCenter(
        -math.cos(this.getBodyAngle.toDouble).toFloat * driveDown,
        -math.sin(this.getBodyAngle.toDouble).toFloat * driveDown,
        true)
    }
    //goes forward
    if(onSand) {
      this.applyBodyForceToCenter(
        math.cos(this.getBodyAngle.toDouble).toFloat * driveUp * sandReductionFactor,
        math.sin(this.getBodyAngle.toDouble).toFloat * driveUp * sandReductionFactor,
        true)
    }
    else if (onGrass){
      this.applyBodyForceToCenter(
        math.cos(this.getBodyAngle.toDouble).toFloat * driveUp * grassReductionFactor,
        math.sin(this.getBodyAngle.toDouble).toFloat * driveUp * grassReductionFactor,
        true)
    }

    else{

      this.applyBodyForceToCenter(
        math.cos(this.getBodyAngle.toDouble).toFloat * driveUp,
        math.sin(this.getBodyAngle.toDouble).toFloat * driveUp,
        true)
    }


    //stops the inertia of the car
    this.applyBodyForceToCenter(
      this.getBodyLinearVelocity.x * -DRAG_THRUST * speed,
      this.getBodyLinearVelocity.y * -DRAG_THRUST * speed,
      true)

    //position of the car
    pos = this.getBodyPosition
    //applies the speed
    g.drawAlphaPicture(pos.x, pos.y, this.getBodyAngleDeg, .5f, 1f, carImage)
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


