package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import ch.hevs.gdx2d.lib.physics.{AbstractPhysicsObject, PhysicsWorld}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{Contact, World}

class Sand(position: Vector2, width: Float, height: Float) extends PhysicsStaticBox("Sand", position, width, height){

  override def endContact(contact: Contact): Unit = {
    val a = contact.getFixtureA.getBody.getUserData
    // val b = contact.getFixtureB.getBody.getUserData
    // @FIXME This is a dirty fix, shall understand why
    println("pouet")
    if (a.isInstanceOf[Sand])
      print(s"we found sand is ${a}")
    // print(s"b is ${b}")
    println("end of contact")
  }



  override def enableCollisionListener(): Unit = {
    val world = PhysicsWorld.getInstance
    world.setContactListener(this)
  }
  
  override def collision(theOtherObject: AbstractPhysicsObject, energy: Float):Unit = {
    /*if (theOtherObject.isInstanceOf[Player]){
      val player : Player = theOtherObject.asInstanceOf[Player]
      player.onSand = true
    }*/
  }

}
