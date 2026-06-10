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
  //getting width and height for more visibility
  var width = Gdx.graphics.getWidth
  var height = Gdx.graphics.getHeight

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

    for (i <- 0 until number_player) {
      //sending the current player that is rendered and the GdxGraphics screen so that it can change the camera accordingly
      setupViewport(i, g)
      //setting the camera on the player position
      g.getCamera.position.set(players(i).getBodyPosition.x, players(i).getBodyPosition.y, 0)
      //needs to update it in order for the camera to actually "move"
      g.getCamera.update()
      //drawing the map
      mapsManager.render(g.getCamera)
      //drawing all the players
      for (j <- 0 until number_player) players(j).draw(g)
      //IMPORTANT we need to tell the program to Flush the current SpriteBatch so that the textures are going to be rendered
      //or else they are staying in the cache and are rendered on the wrong screen
      g.sbFlush()
    }
  }

  def setupViewport(actualPlayer:Int, g:GdxGraphics): Unit = {
    val width = Gdx.graphics.getWidth
    val height = Gdx.graphics.getHeight
    //this is the same for two players because we can just divide the same number by one or two
    if(number_player == 1 || number_player == 2){
      Gdx.gl.glViewport(actualPlayer * width/number_player, 0, width/number_player, height)
      g.getCamera.setToOrtho(false, width * zoom/number_player, height * zoom)
    }
    //since the viewport line changes from 3 players we need to change it for each screen
    else if(number_player == 3 || number_player == 4){
      //first player to be rendered on a 4 screen formation and so on
      if (actualPlayer == 0){
        Gdx.gl.glViewport(0, height/2, width/2, height/2)
      }
      else if (actualPlayer == 1){
        Gdx.gl.glViewport(width/2, height/2, width/2, height/2)
      }
      else if (actualPlayer == 2){
        Gdx.gl.glViewport(0, 0, width/2, height/2)
      }
      else if (actualPlayer == 3){
        Gdx.gl.glViewport(width/2, 0, width/2, height/2)
      }
      //and after we can set the camera because everyone has the same screen size
      g.getCamera.setToOrtho(false, width/2 * zoom, height/2 * zoom)
    }
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
      case _ => ()
    }
  }
}