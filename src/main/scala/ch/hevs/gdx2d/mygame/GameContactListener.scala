package ch.hevs.gdx2d.mygame

import com.badlogic.gdx.physics.box2d.{Contact, ContactImpulse, ContactListener, Manifold}

import scala.collection.mutable.ArrayBuffer

/**
 * The single contact listener for the whole Box2D world.
 *
 * Box2D only allows ONE contact listener per world: every call to
 * `world.setContactListener(...)` REPLACES the previous one. So instead of
 * letting each Wall/Sand register itself (where only the last object created
 * survived, and its callbacks fired for every collision), we install this one
 * listener once in `Game.onInit` and decide here what to do for each contact.
 *
 * Box2D gives us the two objects involved as "fixture A" and "fixture B"
 * without saying which is which, so we react to both orderings. Each body's
 * `userData` is the gdx2d object that created it (a `Player`, `Sand`, `Wall`).
 */
class GameContactListener extends ContactListener {

  // Called once, the moment two objects start touching ("hit").
  override def beginContact(contact: Contact): Unit =
    handle(contact, begin = true)

  // Called once, the moment they stop touching.
  override def endContact(contact: Contact): Unit =
    handle(contact, begin = false)

  /** Look at both objects of the contact and react to each ordering. */
  private def handle(contact: Contact, begin: Boolean): Unit = {
    val a = contact.getFixtureA.getBody.getUserData
    val b = contact.getFixtureB.getBody.getUserData
    react(a, b, begin)
    react(b, a, begin)
  }

  /** React when `self` (the car) touches/leaves `other`. */
  private def react(self: Any, other: Any, begin: Boolean): Unit = self match {
    case p: Player =>
      other match {
        case _: Sand =>

          if (begin) {
            p.onSand = false
            p.onSand = true
          }
          else {
            p.onSand = false
          }

        case _: Grass =>

          if (begin) {
            p.onGrass = false
            p.onGrass = true
          }
          else {
            p.onGrass = false
          }


        case _: Wall =>

        case lilCP: LittleCheckpoint =>
          if (begin) {

            p.stateOfTheCheckpoint(lilCP.c.number) = true

          }

        case _: Finish =>
          if (begin) {
            p.logicForTheFinishBloc(p.stateOfTheCheckpoint,p.nDrivenLapsInClass)
          }

        case _: Player => ()
      }
      
    case _ => () // not the car: ignore
  }

  // Required by the interface but unused here.
  override def preSolve(contact: Contact, oldManifold: Manifold): Unit = ()

  override def postSolve(contact: Contact, impulse: ContactImpulse): Unit = ()
}
