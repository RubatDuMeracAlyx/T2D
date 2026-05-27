package ch.hevs.gdx2d.hello

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
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
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{SelectBox, Skin, TextButton}

import scala.collection.SeqView.DropRight

class Launch extends DesktopApplication(1920, 1080) {

  private var world: World = _
  val assets: GameAssets = new GameAssets
  val mapsManager: MapsManager = new MapsManager
  private var c1: T2DCar = _
  var valor_zoom = 1.5f

  override def onInit(): Unit = {
    setTitle("RACETEST")



    assets.loadAll()
    assets.manager.finishLoading()

    world = PhysicsWorld.getInstance()
    world.setGravity(new Vector2(0f, 0f))

    val loadedMap = assets.getMap()
    mapsManager.load(loadedMap)
    c1 = new T2DCar(new Vector2(getWindowWidth / 2f, getWindowHeight / 2f))
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    PhysicsWorld.updatePhysics(Gdx.graphics.getDeltaTime)

    c1.draw(g)

    val camera: OrthographicCamera = g.getCamera
    camera.position.scl(1, 1, 0)
    g.zoom(0.5f)
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
class Menu extends DesktopApplication(1920, 1080) {
  private var stage: Stage = _
  private var newGameButton: TextButton = _
  private var MapChoice: SelectBox[TextButton] = _
  private var PlayerChoice: SelectBox[TextButton] = _
  private var skin: Skin = _



  override def onInit(): Unit = {
    val buttonWidth = 180f
    val buttonHeight = 30f

    setTitle("Menu")

    stage = new Stage()
    Gdx.input.setInputProcessor(stage)

    skin = new Skin(Gdx.files.internal("ui/uiskin.json"))

    newGameButton = new TextButton("Click me", skin)
    newGameButton.setWidth(buttonWidth)
    newGameButton.setHeight(buttonHeight)

    MapChoice = new SelectBox[TextButton](skin)
    MapChoice.setWidth(buttonWidth)
    MapChoice.setHeight(buttonHeight)

    PlayerChoice = new SelectBox[TextButton](skin)
    PlayerChoice.setWidth(buttonWidth)
    PlayerChoice.setHeight(buttonHeight)

  }
  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear(Color.BLACK)

    stage.act()
    stage.draw()

    g.drawStringCentered(getWindowHeight / 4f, s"Button status ${newGameButton.isChecked}")
    g.drawSchoolLogo()
    g.drawFPS()
  }
  override def onDispose(): Unit = {
    super.onDispose()
    stage.dispose()
    skin.dispose()
  }



}

object Launch {
  def main(args: Array[String]): Unit = {
    new Menu().launch()
   // new Launch().launch()
  }
}
