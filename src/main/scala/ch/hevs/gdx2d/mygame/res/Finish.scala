package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import com.badlogic.gdx.math.Vector2

/**
 * A solid wall tile. Box2D blocks the car against it on its own; contacts are
 * reported centrally in [[GameContactListener]] (giving it its own type lets
 * the listener tell walls and sand apart).
 */
class Finish(position: Vector2, width: Float, height: Float)
  extends PhysicsStaticBox("Finish", position, width, height)
