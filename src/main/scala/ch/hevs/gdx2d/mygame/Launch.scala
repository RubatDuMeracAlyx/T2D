package ch.hevs.gdx2d.hello

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.physics.utils.PhysicsScreenBoundaries
import ch.hevs.gdx2d.demos.physics.car.components.Car
import ch.hevs.gdx2d.desktop.DesktopApplication
import ch.hevs.gdx2d.desktop.physics.DebugRenderer
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.PhysicsWorld
import ch.hevs.gdx2d.mygame.T2DCar
import com.badlogic.gdx.{Gdx, Input}
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World


class Launch extends DesktopApplication(1920, 1080) {


  protected var counter: Float = 0f
  protected var imgBitmap: BitmapImage = _

  private var dbgRenderer: DebugRenderer = _
  private var world: World = _
  private var c1: T2DCar = _

  override def onInit(): Unit = {
    imgBitmap = new BitmapImage("src/main/scala/ch/hevs/gdx2d/mygame/res/image.png")

    world = PhysicsWorld.getInstance()
    world.setGravity(new Vector2(0f, 0f))

    dbgRenderer = new DebugRenderer()

    new PhysicsScreenBoundaries(getWindowWidth.toFloat, getWindowHeight.toFloat)
    c1 = new T2DCar(new Vector2(getWindowWidth / 2f, getWindowHeight / 2f))
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    PhysicsWorld.updatePhysics(Gdx.graphics.getDeltaTime)

    dbgRenderer.render(PhysicsWorld.getInstance(), g.getCamera.combined)
    c1.draw(g)
  }

  override def onKeyUp(keycode: Int): Unit = {
    keycode match {
      case Input.Keys.LEFT  => c1.driftLeft = false
      case Input.Keys.RIGHT => c1.driftRight = false
      case Input.Keys.UP    => c1.driveUp = 0f
      case _ => ()
    }
  }

  override def onKeyDown(keycode: Int): Unit = {
    keycode match {
      case Input.Keys.LEFT  => c1.driftLeft = true
      case Input.Keys.RIGHT => c1.driftRight = true
      case Input.Keys.UP    => c1.driveUp = T2DCar.MAX_THRUST
      case _ => ()
    }
  }
}

object Launch {
  def main(args: Array[String]): Unit = {
    new Launch().launch()
  }
}
