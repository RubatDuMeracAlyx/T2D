package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import ch.hevs.gdx2d.lib.physics.AbstractPhysicsObject
import com.badlogic.gdx.math.Vector2

import scala.collection.mutable.ArrayBuffer

class Checkpoint(val arrB: ArrayBuffer[Vector2], val name:String) {

  var checked: Boolean = false
  createCP()

  def createCP(): Unit = {
    for (i <- arrB){
      val pos : Vector2 = new Vector2(i.x * Map.tileWidth + Map.tileWidth / 2f, i.y * Map.tileHeight + Map.tileHeight / 2f)
      val box = new Box("", pos, Map.tileWidth, Map.tileHeight, this)
      box.enableCollisionListener()
      box.setSensor(false)
    }
  }

}


class Box(name: String, position: Vector2, width: Float, height: Float, c : Checkpoint) extends PhysicsStaticBox(name, position, width, height){

  override def collision(theOtherObject: AbstractPhysicsObject, energy: Float):Unit = {
    //print(c.checked + " ")
    c.checked = true
    //println(c.name + c.checked)

  }

}