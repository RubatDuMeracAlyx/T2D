package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.desktop.DesktopApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Label, SelectBox, Skin, Stack, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Timer



class Menu() extends DesktopApplication(1920, 1080) {
  private var stage: Stage = _
  private var newGameButton: TextButton = _
  private var skin: Skin = _
  private var labelmap: Label = _
  private var labelplayer: Label = _
  private var image : Image = _
  private var background : Texture = _

  private var player1_btn : TextButton = _
  private var player2_btn : TextButton = _
  private var player3_btn : TextButton = _
  private var player4_btn : TextButton = _

  private var map1 : TextButton = _
  private var map2 : TextButton = _

  var nbr_player : Int = 1
  var map_name : String = "firstMap"





  override def onInit(): Unit = {
    val buttonWidth = 300f
    val buttonHeight = 80f
    val selectboxWidth = 200f
    val selectboxHeight = 50f



    setTitle("Menu")

    stage = new Stage()
    Gdx.input.setInputProcessor(stage)

    skin = new Skin(Gdx.files.internal("ui/uiskin.json"))

    background = new Texture(Gdx.files.internal("data/res/MenuT2D.png"))

    newGameButton = new TextButton("New game", skin)
    newGameButton.setWidth(buttonWidth)
    newGameButton.setHeight(buttonHeight)

    map1 = new TextButton("firstMap", skin)
    map1.setWidth(buttonWidth/2)
    map1.setHeight(buttonHeight/2)

    map2 = new TextButton("secondMap", skin)
    map2.setWidth(buttonWidth/2)
    map2.setHeight(buttonHeight/2)


    player1_btn = new TextButton("1 player", skin)
    player1_btn.setWidth(buttonWidth/2)
    player1_btn.setHeight(buttonHeight/2)

    player2_btn = new TextButton("2 player", skin)
    player2_btn.setWidth(buttonWidth/2)
    player2_btn.setHeight(buttonHeight/2)


    player3_btn = new TextButton("3 player", skin)
    player3_btn.setWidth(buttonWidth/2)
    player3_btn.setHeight(buttonHeight/2)

    player4_btn = new TextButton("4 player", skin)
    player4_btn.setWidth(buttonWidth/2)
    player4_btn.setHeight(buttonHeight/2)


    labelmap = new Label("Choose the map", skin)
    labelmap.setWidth(buttonWidth)
    labelmap.setHeight(buttonHeight)

    labelplayer = new Label("How many player", skin)
    labelplayer.setWidth(buttonWidth)
    labelplayer.setHeight(buttonHeight)

    // Set la position des bouton
    newGameButton.setPosition(getWindowWidth / 4f - buttonWidth / 1.9f, getWindowHeight * 0.54f)

    labelmap.setPosition(getWindowWidth / 4f - buttonWidth / 1.89f +80, getWindowHeight * 0.275f )
    map1.setPosition(getWindowWidth / 4f - buttonWidth / 1.89f, getWindowHeight * 0.3f -50)
    map2.setPosition(getWindowWidth / 4f - buttonWidth / 1.89f +160, getWindowHeight * 0.3f -50)

    labelplayer.setPosition(getWindowWidth / 4f - buttonWidth / 1.89f +80, getWindowHeight * 0.43f )
    player1_btn.setPosition(getWindowWidth / 4f - buttonWidth / 1.89f, getWindowHeight * 0.45f -50)
    player2_btn.setPosition(getWindowWidth / 4f - buttonWidth / 1.89f +160, getWindowHeight * 0.45f -50)
    player3_btn.setPosition(getWindowWidth / 4f - buttonWidth / 1.89f, getWindowHeight * 0.45f  -100 )
    player4_btn.setPosition(getWindowWidth / 4f - buttonWidth / 1.89f +160, getWindowHeight * 0.45f -100)


   // set les couleurs des valeurs de base des boutons
    map1.setColor(Color.RED)
    player1_btn.setColor(Color.RED)


    // ajouter les boutons au stage (la fenetre)
    stage.addActor(player1_btn)
    stage.addActor(player2_btn)
    stage.addActor(player3_btn)
    stage.addActor(player4_btn)

    stage.addActor(map1)
    stage.addActor(map2)

    stage.addActor(newGameButton)
    stage.addActor(labelmap)
    stage.addActor(labelplayer)

    // Quand newGame est cliquer il quitte la page menu attend puis lance la game
    newGameButton.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        Gdx.app.exit()
        new Thread(() => {
          Thread.sleep(200)
          var game1: Game = new Game(nbr_player, map_name)
          game1.launch()
        }).start()

      }
    })
    player1_btn.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
          nbr_player = 1
          player1_btn.setColor(Color.RED)
          player2_btn.setColor(Color.valueOf("ffffffff"))
          player3_btn.setColor(Color.valueOf("ffffffff"))
          player4_btn.setColor(Color.valueOf("ffffffff"))



      }
    })
    player2_btn.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
          nbr_player = 2
          player2_btn.setColor(Color.RED)
          player1_btn.setColor(Color.valueOf("ffffffff"))
          player3_btn.setColor(Color.valueOf("ffffffff"))
          player4_btn.setColor(Color.valueOf("ffffffff"))


      }
    })
    player3_btn.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
          nbr_player = 3
          player3_btn.setColor(Color.RED)
          player1_btn.setColor(Color.valueOf("ffffffff"))
          player2_btn.setColor(Color.valueOf("ffffffff"))
          player4_btn.setColor(Color.valueOf("ffffffff"))


      }
    })
    player4_btn.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
          nbr_player = 4
          player4_btn.setColor(Color.RED)
          player1_btn.setColor(Color.valueOf("ffffffff"))
          player2_btn.setColor(Color.valueOf("ffffffff"))
          player3_btn.setColor(Color.valueOf("ffffffff"))


      }
    })
    map1.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        map_name = "firstMap"
        map1.setColor(Color.RED)
        map2.setColor(Color.valueOf("ffffffff"))


      }
    })
    map2.addListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        map_name = "secondMap"
        map2.setColor(Color.RED)
        map1.setColor(Color.valueOf("ffffffff"))


      }
    })
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear(Color.FIREBRICK)
    stage.act()
    stage.getBatch.begin()
    stage.getBatch.draw(background, 0,0,1920,1080)
    stage.getBatch.end()
    stage.draw()
  }

  override def onDispose(): Unit = {

    super.onDispose()
    stage.dispose()
    skin.dispose()
  }


}

object Menu {
  def main(args: Array[String]): Unit = {
    new Menu().launch()
    //new Game(1,"jsp").launch()


  }
}
