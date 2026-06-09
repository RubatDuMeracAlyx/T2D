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
    //clears the frame
    g.clear()
    PhysicsWorld.updatePhysics(Gdx.graphics.getDeltaTime)

    val camera: OrthographicCamera = g.getCamera
    mapsManager.render(camera)



    //draw things here and not anywhere else
    for (i <- 0 until  number_player) {
      players(i).draw(g)

    }
    // draw le time le nombre de tour et la vitesse de la voiture grace a la position du player et le font
    g.drawString(players(debugPlayer).pos.x+1820-150, players(debugPlayer).pos.y+980, timer1.getTime().toString,font)
    g.drawString(players(debugPlayer).pos.x+1820-200, players(debugPlayer).pos.y+980-100, s"tour ${(players(debugPlayer).nDrivenLapsInClass+1).toString}/3", font)
    g.drawString(players(debugPlayer).pos.x+1820-200, players(debugPlayer).pos.y+980-200, s"${(players(debugPlayer).speed*10).toInt} km/h", font)



    g.moveCamera(players(debugPlayer).getBodyPosition.x - 1920 / 2 * zoom, players(debugPlayer).getBodyPosition.y - 1080 / 2 * zoom) //TODO MOOVE ALL THE CAMERA TO EVERY PLAYERS
    g.zoom(zoom)
    camera.update()


    //println(timer1.getTime())

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