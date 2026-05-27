package ch.hevs.gdx2d.hello

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import ch.hevs.gdx2d.components.physics.utils.PhysicsScreenBoundaries
import ch.hevs.gdx2d.demos.physics.car.components.Car
import ch.hevs.gdx2d.desktop.DesktopApplication
import ch.hevs.gdx2d.desktop.physics.DebugRenderer
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.PhysicsWorld
import ch.hevs.gdx2d.mygame.GameAssets
import com.badlogic.gdx.Input.Keys
import ch.hevs.gdx2d.mygame.T2DCar
import com.badlogic.gdx.{Gdx, Input}
import com.badlogic.gdx.graphics.{Color, OrthographicCamera, Texture}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import ch.hevs.gdx2d.mygame.MapsManager
import scala.collection.mutable.ArrayBuffer

class Launch extends DesktopApplication(1920, 1080) {

  private var world: World = _
  val assets: GameAssets = new GameAssets
  val mapsManager: MapsManager = new MapsManager
  private var c1: T2DCar = _
  private var hitboxes: ArrayBuffer[PhysicsStaticBox] = _
  var zoom = 2f

  override def onInit(): Unit = {
    setTitle("RACE")

    assets.loadAll()
    assets.manager.finishLoading()


    world = PhysicsWorld.getInstance()
    world.setGravity(new Vector2(0f, 0f))

    val loadedMap = assets.getMap()
    mapsManager.load(loadedMap)
    hitboxes = assets.generateHitBoxes() // résultat stocké
    c1 = new T2DCar(new Vector2(getWindowWidth / 2f, getWindowHeight / 2f))
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    PhysicsWorld.updatePhysics(Gdx.graphics.getDeltaTime)

    c1.draw(g)

    val camera: OrthographicCamera = g.getCamera
    g.moveCamera(c1.box.getBodyPosition.x-1920/2*zoom,c1.box.getBodyPosition.y-1080/2*zoom)
    g.zoom(zoom)
    camera.update()

    mapsManager.render(camera)

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
