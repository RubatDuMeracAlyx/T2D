package ch.hevs.gdx2d.hello

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import ch.hevs.gdx2d.components.physics.utils.PhysicsScreenBoundaries
import ch.hevs.gdx2d.demos.physics.car.components.Car
import ch.hevs.gdx2d.desktop.DesktopApplication
import ch.hevs.gdx2d.desktop.physics.DebugRenderer
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.PhysicsWorld
import ch.hevs.gdx2d.lib.utils.Logger
import ch.hevs.gdx2d.mygame.GameAssets
import com.badlogic.gdx.Input.Keys
import ch.hevs.gdx2d.mygame.T2DCar
import com.badlogic.gdx.{Gdx, Input}
import com.badlogic.gdx.graphics.{Color, OrthographicCamera, Texture}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import ch.hevs.gdx2d.mygame.MapsManager
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle
import com.badlogic.gdx.scenes.scene2d.ui.{Label, SelectBox, Skin, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

import scala.collection.SeqView.DropRight
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
    new PhysicsScreenBoundaries(getWindowWidth.toFloat, getWindowHeight.toFloat)

    c1 = new T2DCar(new Vector2(assets.spawnPlacementForTheCar()(0)))
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
class Menu() extends DesktopApplication(1920, 1080) {
  private var stage: Stage = _
  private var newGameButton: TextButton = _
  private var MapChoice: SelectBox[Int] = _
  private var PlayerChoice: SelectBox[Int] = _
  private var skin: Skin = _
  private var labelmap : Label = _
  private var labelplayer : Label = _
  val boxStyle = new SelectBoxStyle()



  override def onInit(): Unit = {
    val buttonWidth = 180f
    val buttonHeight = 30f
    val selectboxWidth = 200f
    val  selectboxHeight = 50f


    setTitle("Menu")

    stage = new Stage()
    Gdx.input.setInputProcessor(stage)

    skin = new Skin(Gdx.files.internal("ui/uiskin.json"))

    newGameButton = new TextButton("New game", skin)
    newGameButton.setWidth(buttonWidth)
    newGameButton.setHeight(buttonHeight)

    labelmap = new Label("Choose the map", skin)
    labelmap.setWidth(buttonWidth)
    labelmap.setHeight(buttonHeight)

    labelplayer = new Label("How many player", skin)
    labelplayer.setWidth(buttonWidth)
    labelplayer.setHeight(buttonHeight)

    MapChoice = new SelectBox[Int](skin)
    MapChoice.setWidth(selectboxWidth)
    MapChoice.setHeight(selectboxHeight)

    PlayerChoice = new SelectBox[Int](skin)
    PlayerChoice.setWidth(selectboxWidth+100)
    PlayerChoice.setHeight(selectboxHeight)


    //nombre de choix des selectbox
    MapChoice.setMaxListCount(2)
    PlayerChoice.setMaxListCount(4)

    //choix en question
    MapChoice.setItems(1,2)
    PlayerChoice.setItems(1,2,3,4)



    // Set la position des bouton
    newGameButton.setPosition(getWindowWidth / 2f - buttonWidth / 2f, getWindowHeight * 0.6f)
    MapChoice.setPosition(getWindowWidth / 2f - buttonWidth / 2f, getWindowHeight * 0.4f)
    PlayerChoice.setPosition(getWindowWidth / 2f - buttonWidth / 2f, getWindowHeight * 0.7f)
    labelmap.setPosition(getWindowWidth / 2f - buttonWidth / 2f, getWindowHeight * 0.7f+50)
    labelplayer.setPosition(getWindowWidth / 2f - buttonWidth / 2f, getWindowHeight * 0.4f+50)



    // ajouter les boutons au stage (la fenetre)

    stage.addActor(newGameButton)
    stage.addActor(MapChoice)
    stage.addActor(PlayerChoice)
    stage.addActor(labelmap)
    stage.addActor(labelplayer)

    // Quand newGame est cliquer il quitte la page menu attend puis lance la game
    newGameButton.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        Gdx.app.exit()
        new Thread(() => {
          Thread.sleep(100)
          new Launch().launch()
        }).start()

      }
    })
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
       g.clear(Color.FIREBRICK)

      stage.act()
      stage.draw()

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

  }
}
