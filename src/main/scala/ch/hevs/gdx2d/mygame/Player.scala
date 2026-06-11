package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.physics.primitives.PhysicsBox
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.AbstractPhysicsObject
import ch.hevs.gdx2d.mygame.Car.{BoostValue, DRAG_THRUST, DRAG_TORQUE, MAX_TORQUE}
import com.badlogic.gdx.math.Vector2

import scala.collection.mutable.ArrayBuffer

class Player(var playerNbr : Int ,var position: Vector2, nCheckpoints: Int, var timer_player: Timer) extends PhysicsBox(playerNbr.toString, position, 150f, 45f, math.toRadians(90.0).toFloat){
  var accelerate: Boolean = false
  val carImage = new BitmapImage("data/res/CARS/Car" + (playerNbr + 1) + ".png")
  var driveUp = 0f
  var driftLeft = false
  var driftRight = false
  var driveDown = 0f
  var reset: Boolean = false
  var fuel = 30
  var actualCP : Int = -1
  var needStock = false // check if we need to stock position, speed... for respawn
  var boost = false
  var speed = 0f
  var stateOfTheCheckpoint : ArrayBuffer[Boolean] = ArrayBuffer.empty
  var nDrivenLapsInClass : Int = 0
  var finished : Boolean = false
  var onSand = false
  val sandReductionFactor = 0.3f
  var onGrass = false
  val grassReductionFactor = 0.2f
  var pos:Vector2 = _
  var stockVal : (Vector2,Vector2,Int,Float,Float) = stockForReset()

  createTheStateOfTheCheckpoint(nCheckpoints)

  // TODO def everyoneFinished (arrayBuffer: ArrayBuffer[Player]) : Unit = {}


    def stockForReset () : (Vector2,Vector2,Int,Float,Float )  = { // func to stock a bunch of variable in order to make the reset
    val position : Vector2 = new Vector2(this.getBodyTransform.getPosition)
    val linearVelocity : Vector2 = new Vector2 (this.getBodyLinearVelocity)
    val fuel: Int = this.fuel
    val angle: Float = this.getBodyAngle.toDouble.toFloat
    val BodyAngularVelocity : Float = this.getBodyAngularVelocity
    (position,linearVelocity,fuel,angle,BodyAngularVelocity)
  }

  def addBoost (amount : Int) : Unit = {
    fuel = fuel + amount
  }

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

    if (needStock){
      stockVal = stockForReset()
      needStock = false
    }

    if (!reset) {
      speed = getDistanceVector(this)
      //booster
      if (boost && fuel > 0) {
        this.applyBodyForceToCenter(
          math.cos(this.getBodyAngle.toDouble).toFloat * BoostValue,
          math.sin(this.getBodyAngle.toDouble).toFloat * BoostValue,
          true)
        fuel -= 1
      }

      //turns the car
      if (driftLeft) this.applyBodyTorque(MAX_TORQUE * speed, true)
      if (driftRight) this.applyBodyTorque(-MAX_TORQUE * speed, true)
      //stop the inertia of the turn
      if (this.getBodyAngularVelocity > 0) this.applyBodyTorque(-DRAG_TORQUE, true)
      if (this.getBodyAngularVelocity < 0) this.applyBodyTorque(DRAG_TORQUE, true)

      //goes backward
      if (onSand) {
        this.applyBodyForceToCenter(
          -math.cos(this.getBodyAngle.toDouble).toFloat * driveDown * sandReductionFactor,
          -math.sin(this.getBodyAngle.toDouble).toFloat * driveDown * sandReductionFactor,
          true)
      }
      else if (onGrass) {
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
      if (onSand) {
        this.applyBodyForceToCenter(
          math.cos(this.getBodyAngle.toDouble).toFloat * driveUp * sandReductionFactor,
          math.sin(this.getBodyAngle.toDouble).toFloat * driveUp * sandReductionFactor,
          true)
      }
      else if (onGrass) {
        this.applyBodyForceToCenter(
          math.cos(this.getBodyAngle.toDouble).toFloat * driveUp * grassReductionFactor,
          math.sin(this.getBodyAngle.toDouble).toFloat * driveUp * grassReductionFactor,
          true)
      }

      else {

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
    else {
      this.applyBodyForceToCenter(stockVal._1.x * -DRAG_THRUST * speed, stockVal._1.y * -DRAG_THRUST * speed, true)
      this.getBody.setLinearVelocity(0, 0)
      this.getBody.setAngularVelocity(0)
      this.getBody.setTransform(new Vector2 (stockVal._1.x, stockVal._1.y), stockVal._4)
      fuel = stockVal._3
      reset = false
    }
  }
  def getDistanceVector(box:PhysicsBox):Float={
    return (Math.sqrt(Math.pow(box.getBodyLinearVelocity.x,2)+Math.pow(box.getBodyLinearVelocity.y,2)).toFloat)
  }
} 



object Car {
  var MAX_THRUST = 35f
  var MAX_TORQUE = 0.45f
  val DRAG_THRUST = 0.6f
  val DRAG_TORQUE = 1.3f
  val BoostValue = 105f

}


