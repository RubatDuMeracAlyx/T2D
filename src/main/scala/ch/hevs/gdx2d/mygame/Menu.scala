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
import ch.hevs.gdx2d.mygame.{Game, GameAssets, MapsManager, T2DCar}
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.{Gdx, Input}
import com.badlogic.gdx.graphics.{Color, OrthographicCamera, Texture}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle
import com.badlogic.gdx.scenes.scene2d.ui.{Label, SelectBox, Skin, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

import scala.collection.SeqView.DropRight
import scala.collection.mutable.ArrayBuffer



class Menu() extends DesktopApplication(1920, 1080) {
  private var stage: Stage = _
  private var newGameButton: TextButton = _
  private var mapChoice: SelectBox[String] = _
  private var playerChoice: SelectBox[Int] = _
  private var skin: Skin = _
  private var labelmap: Label = _
  private var labelplayer: Label = _
  val boxStyle = new SelectBoxStyle()


  override def onInit(): Unit = {
    val buttonWidth = 180f
    val buttonHeight = 30f
    val selectboxWidth = 200f
    val selectboxHeight = 50f


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

    mapChoice = new SelectBox[String](skin)
    mapChoice.setWidth(selectboxWidth)
    mapChoice.setHeight(selectboxHeight)

    playerChoice = new SelectBox[Int](skin)
    playerChoice.setWidth(selectboxWidth + 100)
    playerChoice.setHeight(selectboxHeight)


    //nombre de choix des selectbox
    mapChoice.setMaxListCount(2)
    playerChoice.setMaxListCount(4)

    //choix en question
    mapChoice.setItems("demo","jsp")
    playerChoice.setItems(1, 2, 3, 4)



    // Set la position des bouton
    newGameButton.setPosition(getWindowWidth / 2f - buttonWidth / 2f, getWindowHeight * 0.6f)
    mapChoice.setPosition(getWindowWidth / 2f - buttonWidth / 2f, getWindowHeight * 0.4f)
    playerChoice.setPosition(getWindowWidth / 2f - buttonWidth / 2f, getWindowHeight * 0.7f)
    labelmap.setPosition(getWindowWidth / 2f - buttonWidth / 2f, getWindowHeight * 0.7f + 50)
    labelplayer.setPosition(getWindowWidth / 2f - buttonWidth / 2f, getWindowHeight * 0.4f + 50)



    // ajouter les boutons au stage (la fenetre)

    stage.addActor(newGameButton)
    stage.addActor(mapChoice)
    stage.addActor(playerChoice)
    stage.addActor(labelmap)
    stage.addActor(labelplayer)

    // Quand newGame est cliquer il quitte la page menu attend puis lance la game
    newGameButton.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        Gdx.app.exit()
        new Thread(() => {
          Thread.sleep(100)
          var game1: Game = new Game(playerChoice.getSelected, mapChoice.getSelected)
          game1.launch()
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

object Menu {
  def main(args: Array[String]): Unit = {
    new Game(2,"67").launch()

  }
}
