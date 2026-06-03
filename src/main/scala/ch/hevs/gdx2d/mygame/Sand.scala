package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import com.badlogic.gdx.math.Vector2

/**
 * A patch of sand on the track.
 *
 * Sand does not handle its own contacts: Box2D allows a single contact
 * listener for the whole world, so all contact handling lives in
 * [[GameContactListener]]. Sand bodies are created as sensors in
 * `Map.generateSand` so the car drives over them instead of bouncing off.
 */
class Sand(position: Vector2, width: Float, height: Float)
  extends PhysicsStaticBox("Sand", position, width, height)
