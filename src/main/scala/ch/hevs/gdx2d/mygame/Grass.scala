package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import com.badlogic.gdx.math.Vector2

class Grass (position: Vector2, width: Float, height: Float)
  extends PhysicsStaticBox("Grass", position, width, height)
