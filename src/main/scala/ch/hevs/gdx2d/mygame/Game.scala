package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import ch.hevs.gdx2d.desktop.DesktopApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.PhysicsWorld
import com.badlogic.gdx.{Gdx, Input}
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import scala.collection.mutable.ArrayBuffer

class Game(var number_player: Int, var map_name: String) extends DesktopApplication(1920, 1080) {

  //putting all the variables that will change later here
  private var world: World = _
  val assets: Map = new Map("firstMap")
  val mapsManager: MapsManager = new MapsManager
  //to move later (one for each player)
  private var c1: T2DCar = _
  //for the walls
  private var hitboxes: ArrayBuffer[PhysicsStaticBox] = _
  //camera zoom (to change later)
  var zoom = 2f

  var timer1:Timer = _


  override def onInit(): Unit = {
    setTitle(map_name)
    //loads the assets
    assets.loadAll()
    assets.manager.finishLoading()

    //applying forces to the world and removing gravity (top view game)
    world = PhysicsWorld.getInstance()
    world.setGravity(new Vector2(0f, 0f))

    //load the whole map
    mapsManager.load(assets.getMap())
    //find the checkpoints and group them (depending on the map) and add a hitbox on them
    assets.findCheckPoints(assets.findCheckPointBlocksCoords())
    //generate the walls
    hitboxes = assets.generateHitBoxes()
    //creates the car (to change depending on player
    c1 = new T2DCar(new Vector2(assets.spawnPlacementForTheCar()(0)))
    timer1 = new Timer
  }

  //every frame
  override def onGraphicRender(g: GdxGraphics): Unit = {
    //clears the frame
    g.clear()
    PhysicsWorld.updatePhysics(Gdx.graphics.getDeltaTime)

    c1.draw(g)

    val camera: OrthographicCamera = g.getCamera
    g.moveCamera(c1.box.getBodyPosition.x - 1920 / 2 * zoom, c1.box.getBodyPosition.y - 1080 / 2 * zoom)
    g.zoom(zoom)
    camera.update()

    mapsManager.render(camera)
    println(timer1.getTime())
  }

  override def onKeyUp(keycode: Int): Unit = {
    keycode match {
      case Input.Keys.LEFT => c1.driftLeft = false
      case Input.Keys.RIGHT => c1.driftRight = false
      case Input.Keys.UP => c1.driveUp = 0f
      case Input.Keys.DOWN => c1.driveDown = 0f
      case Input.Keys.SPACE => c1.boost = false
      //case Input.Keys.R => c1.derapage = false
      case _ => ()
    }
  }

  override def onKeyDown(keycode: Int): Unit = {
    keycode match {
      case Input.Keys.LEFT => c1.driftLeft = true
      case Input.Keys.RIGHT => c1.driftRight = true
      case Input.Keys.UP => c1.driveUp = T2DCar.MAX_THRUST
      case Input.Keys.DOWN => c1.driveDown = T2DCar.MAX_THRUST
      case Input.Keys.SPACE => c1.boost = true
      //case Input.Keys.R => c1.derapage = true
      case _ => ()
    }
  }
}