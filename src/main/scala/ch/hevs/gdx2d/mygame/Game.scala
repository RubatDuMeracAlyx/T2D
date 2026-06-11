package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
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
  private val nBoost : Int = 50
  private var boosts : Array[Boost] = Array.ofDim(nBoost)
  //for the walls
  private var hitboxes: ArrayBuffer[PhysicsStaticBox] = ArrayBuffer[PhysicsStaticBox]()
  //camera zoom (to change later)
  var zoom = 2f
  var timer1 = new Timer()
  var debugPlayer = 0
  private var font: BitmapFont = _
  private var font2: BitmapFont = _
  private var finished_time : Double = _
  var image_finished: BitmapImage = _

  //getting width and height for more visibility
  var width = Gdx.graphics.getWidth
  var height = Gdx.graphics.getHeight
  var tab_timer : ArrayBuffer[Timer]= new ArrayBuffer[Timer]()
  for(i<-0 until number_player){
    var timer = new Timer
    tab_timer.append(timer)
  }


  override def onInit(): Unit = {
    setTitle(map_name)
    //loads the assets
    assets.loadAll()
    assets.manager.finishLoading()
    val optimusF = Gdx.files.internal("font/OptimusPrinceps.ttf")
    val icePixelF = Gdx.files.internal("font/ice_pixel-7.ttf")

    image_finished = new BitmapImage("data/res/T2D_finished.png")
    val parameter = new FreeTypeFontParameter

    var generator = new FreeTypeFontGenerator(optimusF)
    parameter.size = generator.scaleForPixelHeight(60)
    parameter.color = Color.BLACK
    font = generator.generateFont(parameter)

    generator = new FreeTypeFontGenerator(icePixelF)
    parameter.size = generator.scaleForPixelHeight(80)
    parameter.color = Color.WHITE
    parameter.borderColor = Color.RED
    parameter.borderWidth = 3f
    parameter.shadowOffsetY = -8
    parameter.shadowColor = Color.LIGHT_GRAY
    font2 = generator.generateFont(parameter)
    generator.dispose()

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
      players(i) = new Player(i,new Vector2(assets.createSpawnPlacementAndFinishForTheCar()(i*3)) , checkpoints.length, tab_timer(i))
    }
    //generate 10 boost fuel
    boosts = assets.createNBoost(nBoost)
  }

  //every frame
  override def onGraphicRender(g: GdxGraphics): Unit = {
    //clears the whole screen
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
      for (j <- 0 until number_player) {
        if(players(j).finished && players(j)!=null) {
          //si le joueur atteint l'arrivéé arrete de le dessiner et enleve ces collisions et le stoppe
          players(j).setSensor(false)
          players(j).setBodyAwake(false)
        }
        else{
          players(j).draw(g)
        }
      }

      for (i <- boosts.indices) {
        if (!boosts(i).undraw) {
          boosts(i).draw(g)
        }
      }

      display_hud(i,g)
      //IMPORTANT we need to tell the program to Flush the current SpriteBatch so that the textures are going to be rendered
      //or else they are staying in the cache and are rendered on the wrong screen
      g.sbFlush()
    }


  }

  def setupViewport(actualPlayer: Int, g: GdxGraphics): Unit = {
    val width = Gdx.graphics.getWidth
    val height = Gdx.graphics.getHeight
    //this is the same for two players because we can just divide the same number by one or two
    if (number_player == 1 || number_player == 2) {
      Gdx.gl.glViewport(actualPlayer * width / number_player, 0, width / number_player, height)
      g.getCamera.setToOrtho(false, width * zoom / number_player, height * zoom)
    }
    //since the viewport line changes from 3 players we need to change it for each screen
    else if (number_player == 3 || number_player == 4) {
      //first player to be rendered on a 4 screen formation and so on
      if (actualPlayer == 0) {
        Gdx.gl.glViewport(0, height / 2, width / 2, height / 2)
      }
      else if (actualPlayer == 1) {
        Gdx.gl.glViewport(width / 2, height / 2, width / 2, height / 2)
      }
      else if (actualPlayer == 2) {
        Gdx.gl.glViewport(0, 0, width / 2, height / 2)
      }
      else if (actualPlayer == 3) {
        Gdx.gl.glViewport(width / 2, 0, width / 2, height / 2)
      }
      //and after we can set the camera because everyone has the same screen size
      g.getCamera.setToOrtho(false, width / 2 * zoom, height / 2 * zoom)
    }
  }
  def display_hud(actualPlayer: Int,g:GdxGraphics):Unit ={
    var additionalheight = 1620
    var additionalwidth = 980

    if(number_player==1){
      // si le joueur a finit affiche l'ecran de fin et son temps la taille diffère en fct du nbr de joueur
      if (players(actualPlayer).finished == false) {
        // affiche le temps durant la course
        g.drawString(players(actualPlayer).pos.x+additionalheight, players(actualPlayer).pos.y+additionalwidth, timer1.getTime().toString, font)
      }
      else{
         finished_time = players(actualPlayer).timer_player.stopTime()
        g.drawPicture(players(actualPlayer).pos.x, players(actualPlayer).pos.y, image_finished)
        g.drawString(players(actualPlayer).pos.x, players(actualPlayer).pos.y, finished_time.toString, font)
        g.drawString(players(actualPlayer).pos.x-500, players(actualPlayer).pos.y-100, "press escape to go back to menu", font2)
      }
      // la vitesse et les tour sont tout le tems affichés
      g.drawString(players(actualPlayer).pos.x+additionalheight, players(actualPlayer).pos.y+additionalwidth-100 , s"tour ${(players(actualPlayer).nDrivenLapsInClass+1).toString}/3", font)
      g.drawString(players(actualPlayer).pos.x+additionalheight, players(actualPlayer).pos.y+additionalwidth-200, s"${(players(actualPlayer).speed * 10).toInt} km/h", font)
      g.drawString(players(actualPlayer).pos.x + additionalheight, players(actualPlayer).pos.y + additionalwidth - 300, s"Fuel : ${players(actualPlayer).fuel}", font)

    }
    if(number_player==2){
      if (players(actualPlayer).finished == false) {
        g.drawString(players(actualPlayer).pos.x+additionalheight/2-100, players(actualPlayer).pos.y+additionalwidth, timer1.getTime().toString, font)
      }
      else {
        finished_time = players(actualPlayer).timer_player.stopTime()
        g.drawPicture(players(actualPlayer).pos.x, players(actualPlayer).pos.y, image_finished)
        g.drawString(players(actualPlayer).pos.x, players(actualPlayer).pos.y, finished_time.toString, font)
        g.drawString(players(actualPlayer).pos.x-500, players(actualPlayer).pos.y-100, "press escape to go back to menu", font2)
      }

      g.drawString(players(actualPlayer).pos.x+additionalheight/2-100 , players(actualPlayer).pos.y+additionalwidth-100, s"tour ${(players(actualPlayer).nDrivenLapsInClass+1).toString}/3", font)
      g.drawString(players(actualPlayer).pos.x+additionalheight/2-100, players(actualPlayer).pos.y+additionalwidth-200, s"${(players(actualPlayer).speed * 10).toInt} km/h", font)
      g.drawString(players(actualPlayer).pos.x + additionalheight / 2 - 100, players(actualPlayer).pos.y + additionalwidth - 300, s"Fuel : ${players(actualPlayer).fuel}", font)
    }
    if(number_player==3 || number_player==4){
      if (players(actualPlayer).finished == false) {
        g.drawString(players(actualPlayer).pos.x+additionalheight/2-100, players(actualPlayer).pos.y+additionalwidth/2, timer1.getTime().toString, font)
      }
      else {
         finished_time = players(actualPlayer).timer_player.stopTime()
        g.drawPicture(players(actualPlayer).pos.x, players(actualPlayer).pos.y, image_finished)
        g.drawString(players(actualPlayer).pos.x, players(actualPlayer).pos.y, finished_time.toString, font)
        g.drawString(players(actualPlayer).pos.x-500, players(actualPlayer).pos.y-100, "press escape to go back to menu", font2)
      }

      g.drawString(players(actualPlayer).pos.x+additionalheight/2-100, players(actualPlayer).pos.y+additionalwidth/2-100, s"tour ${(players(actualPlayer).nDrivenLapsInClass+1).toString}/3", font)
      g.drawString(players(actualPlayer).pos.x+additionalheight/2-100, players(actualPlayer).pos.y+additionalwidth/2-200, s"${(players(actualPlayer).speed * 10).toInt} km/h", font)
      g.drawString(players(actualPlayer).pos.x + additionalheight / 2 - 100, players(actualPlayer).pos.y + additionalwidth / 2 - 300, s"Fuel : ${players(actualPlayer).fuel}", font)

    }
  }

  override def onKeyUp(keycode: Int): Unit = {
    keycode match {
      //player 1
      case Input.Keys.E => players(debugPlayer).reset = false
      case Input.Keys.A => players(debugPlayer).driftLeft = false
      case Input.Keys.D => players(debugPlayer).driftRight = false
      case Input.Keys.W => players(debugPlayer).driveUp = 0f
      case Input.Keys.S => players(debugPlayer).driveDown = 0f
      case Input.Keys.Q => players(debugPlayer).boost = false

      //player 2
      case Input.Keys.O if number_player > 1 => players(1).reset = false
      case Input.Keys.J if number_player > 1 => players(1).driftLeft = false
      case Input.Keys.L if number_player > 1 => players(1).driftRight = false
      case Input.Keys.I if number_player > 1 => players(1).driveUp = 0f
      case Input.Keys.K if number_player > 1 => players(1).driveDown = 0f
      case Input.Keys.U if number_player > 1 => players(1).boost = false

      //player 3
      case Input.Keys.Y if number_player > 2 => players(2).reset = false
      case Input.Keys.F if number_player > 2 => players(2).driftLeft = false
      case Input.Keys.H if number_player > 2 => players(2).driftRight = false
      case Input.Keys.T if number_player > 2 => players(2).driveUp = 0f
      case Input.Keys.G if number_player > 2 => players(2).driveDown = 0f
      case Input.Keys.R if number_player > 2 => players(2).boost = false

      //player 4
      case Input.Keys.SHIFT_RIGHT if number_player > 3 => players(3).reset = false
      case Input.Keys.LEFT if number_player > 3 => players(3).driftLeft = false
      case Input.Keys.RIGHT if number_player > 3 => players(3).driftRight = false
      case Input.Keys.UP if number_player > 3 => players(3).driveUp = 0f
      case Input.Keys.DOWN if number_player > 3 => players(3).driveDown = 0f
      case Input.Keys.ENTER if number_player > 3 => players(3).boost = false

      case _ => ()
    }
  }

  override def onKeyDown(keycode: Int): Unit = {
    keycode match {
      //player 1
      case Input.Keys.E => players(0).reset = true
      case Input.Keys.A => players(0).driftLeft = true
      case Input.Keys.D => players(0).driftRight = true
      case Input.Keys.W => players(0).driveUp = Car.MAX_THRUST
      case Input.Keys.S => players(0).driveDown = Car.MAX_THRUST
      case Input.Keys.Q => players(0).boost = true

      //player 2
      case Input.Keys.O if number_player > 1 => players(1).reset = true
      case Input.Keys.J if number_player > 1 => players(1).driftLeft = true
      case Input.Keys.L if number_player > 1 => players(1).driftRight = true
      case Input.Keys.I if number_player > 1 => players(1).driveUp = Car.MAX_THRUST
      case Input.Keys.K if number_player > 1 => players(1).driveDown = Car.MAX_THRUST
      case Input.Keys.U if number_player > 1 => players(1).boost = true

      //player 3
      case Input.Keys.Y if number_player > 2 => players(2).reset = true
      case Input.Keys.F if number_player > 2 => players(2).driftLeft = true
      case Input.Keys.H if number_player > 2 => players(2).driftRight = true
      case Input.Keys.T if number_player > 2 => players(2).driveUp = Car.MAX_THRUST
      case Input.Keys.G if number_player > 2 => players(2).driveDown = Car.MAX_THRUST
      case Input.Keys.R if number_player > 2 => players(2).boost = true


      //player 4
      case Input.Keys.SHIFT_RIGHT if number_player > 3 => players(3).reset = true
      case Input.Keys.LEFT if number_player > 3 => players(3).driftLeft = true
      case Input.Keys.RIGHT if number_player > 3 => players(3).driftRight = true
      case Input.Keys.UP if number_player > 3 => players(3).driveUp = Car.MAX_THRUST
      case Input.Keys.DOWN if number_player > 3 => players(3).driveDown = Car.MAX_THRUST
      case Input.Keys.ENTER if number_player > 3 => players(3).boost = true

      //exit
      case Input.Keys.ESCAPE => Gdx.app.exit()
        new Thread(() => {
          Thread.sleep(400)
          var menu: Menu = new Menu()
          menu.launch()
        }).start()
      case _ => ()
    }
  }
}