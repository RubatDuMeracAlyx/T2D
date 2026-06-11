package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import ch.hevs.gdx2d.lib.physics.AbstractPhysicsObject
import com.badlogic.gdx.math.Vector2

import scala.collection.mutable.ArrayBuffer

class Checkpoint(val arrB: ArrayBuffer[Vector2], val number: Int) {

  var checked: Boolean = false
  createCP()

  def createCP(): Unit = { // Create checkpoint hitbox
    for (i <- arrB) {
      val pos: Vector2 = new Vector2(i.x * Map.tileWidth + Map.tileWidth / 2f, i.y * Map.tileHeight + Map.tileHeight / 2f)
      val box = new LittleCheckpoint("", pos, Map.tileWidth, Map.tileHeight, this)
      box.enableCollisionListener()
      box.setSensor(false)
    }
  }
}


class LittleCheckpoint(name: String, val position: Vector2, val width: Float, val height: Float, val c: Checkpoint) extends PhysicsStaticBox(name, position, width, height)
// LittleCheckpoint represent any bloc of checkpoint (checkpoint tiles in tiled)
