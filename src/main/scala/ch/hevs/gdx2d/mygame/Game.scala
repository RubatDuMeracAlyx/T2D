package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import ch.hevs.gdx2d.desktop.DesktopApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.PhysicsWorld
import com.badlogic.gdx.{Gdx, Input}
import com.badlogic.gdx.graphics.{Color, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.StretchViewport

import scala.collection.mutable.ArrayBuffer

class Game(var number_player: Int, var map_name: String) extends DesktopApplication(1920, 1080) {

  //putting all the variables that will change later here
  private var world: World = _
  val assets: Map = new Map(map_name)
  val mapsManager: MapsManager = new MapsManager
  //to move later (one for each player)
  private var players: Array[Player] = Array.ofDim(number_player)
  //for the walls
  private var hitboxes: ArrayBuffer[PhysicsStaticBox] = ArrayBuffer[PhysicsStaticBox]()
  //camera zoom (to change later)
  var zoom = 2f
  var timer1 = new Timer()
  var debugPlayer = 0
  private var font: BitmapFont = _

  var viewP1 = new StretchViewport(800, 400)
  var viewP2 = new StretchViewport(800, 400)

  override def onInit(): Unit = {
    setTitle(map_name)
    //loads the assets
    assets.loadAll()
    assets.manager.finishLoading()
    val optimusF = Gdx.files.internal("font/OptimusPrinceps.ttf")

    val parameter = new FreeTypeFontParameter

    var generator = new FreeTypeFontGenerator(optimusF)
    parameter.size = generator.scaleForPixelHeight(60)
    parameter.color = Color.BLACK
    font = generator.generateFont(parameter)


    //applying forces to the world and removing gravity (top view game)
    world = PhysicsWorld.getInstance()
    world.setGravity(new Vector2(0f, 0f))

    //load the whole map
    mapsManager.load(assets.getMap())
    //find the checkpoints and group them (depending on the map) and add a hitbox on them
    val checkpoints = assets.findCheckPoints(assets.findCheckPointBlocksCoords())
    //generate the walls
    assets.generateHitBoxes()
    //generate the sand and grass
    assets.createSand()
    assets.createGrass()
    //install the SINGLE contact listener for the whole physics world
    world.setContactListener(new GameContactListener)
    //creates the car (to change depending on player

    for (i <- 0 until number_player){
      players(i) = new Player(i,new Vector2(assets.createSpawnPlacementAndFinishForTheCar()(i)) , checkpoints.length)
    }
  }

  //every frame
  override def onGraphicRender(g: GdxGraphics): Unit = {
    //clear the whole screen
    g.clear()
    //updates physics
    PhysicsWorld.updatePhysics(Gdx.graphics.getDeltaTime)

    //get windows size values
    val camera = g.getCamera

    //player 1
    //makes it so that it draws only on the left part of the screen
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth/2, Gdx.graphics.getHeight)
    //moving camera on the first player
    g.moveCamera(players(0).getBodyPosition.x - 1920 / 2 * zoom, players(0).getBodyPosition.y - 1080 / 2 * zoom)
    g.zoom(zoom)
    //apply changes
    camera.update()

    //draws the map seen from the first player viewpoint
    mapsManager.render(camera)
    for (i <- 0 until number_player) {
      players(i).draw(g)
    }

    /*Tried to move the text where it's supposed to go but it appears to everyone
    g.drawString(player(0).pos.x + 1820 - 150, player(0).pos.y + 980, timer1.getTime().toString, font)
    g.drawString(player(0).pos.x + 1820 - 200, player(0).pos.y + 980 - 100, s"tour ${(p1.nDrivenLapsInClass + 1).toString}/3", font)
    g.drawString(player(0).pos.x + 1820 - 200, player(0).pos.y + 980 - 200, s"${(p1.speed * 10).toInt} km/h", font)*/


    //player 2
    Gdx.gl.glViewport(Gdx.graphics.getWidth/2, 0, Gdx.graphics.getWidth/2, Gdx.graphics.getHeight)

    val p2 = players(1)
    g.moveCamera(p2.getBodyPosition.x - 1920 / 4 * zoom, p2.getBodyPosition.y - 1080 / 2 * zoom)
    g.zoom(zoom)
    camera.update()

    mapsManager.render(camera)
    for (i <- 0 until number_player) {
      players(i).draw(g)
    }




    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth, Gdx.graphics.getHeight)

  }

  override def onKeyUp(keycode: Int): Unit = {
    keycode match {
      case Input.Keys.LEFT => players(debugPlayer).driftLeft = false
      case Input.Keys.RIGHT => players(debugPlayer).driftRight = false
      case Input.Keys.UP => players(debugPlayer).driveUp = 0f
      case Input.Keys.DOWN => players(debugPlayer).driveDown = 0f
      case Input.Keys.SPACE => players(debugPlayer).boost = false
      //case Input.Keys.R => players(0).derapage = false
      case _ => ()
    }
  }

  override def onKeyDown(keycode: Int): Unit = {
    keycode match {
      case Input.Keys.LEFT => players(debugPlayer).driftLeft = true
      case Input.Keys.RIGHT => players(debugPlayer).driftRight = true
      case Input.Keys.UP => players(debugPlayer).driveUp = Car.MAX_THRUST
      case Input.Keys.DOWN => players(debugPlayer).driveDown = Car.MAX_THRUST
      case Input.Keys.SPACE => players(debugPlayer).boost = true
      case Input.Keys.ESCAPE => Gdx.app.exit()
        new Thread(() => {
          Thread.sleep(200)
          var menu : Menu= new Menu()
          menu.launch()
        }).start()
      //case Input.Keys.R => players(0).derapage = true
      case _ => ()
    }
  }
}