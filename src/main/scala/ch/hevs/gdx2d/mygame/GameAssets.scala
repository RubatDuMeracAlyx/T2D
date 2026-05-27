package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.components.physics.primitives.{PhysicsBox, PhysicsStaticBox}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer, TmxMapLoader}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable

import scala.collection.mutable.ArrayBuffer

class GameAssets extends Disposable {
  val manager: AssetManager = new AssetManager()

  private val MAP_PATH    = "data/res/firstMap.tmx"


  def loadAll(): Unit = {
    manager.setLoader(classOf[TiledMap], new TmxMapLoader(new InternalFileHandleResolver))
    manager.load(MAP_PATH, classOf[TiledMap])
  }

  def updateLoading(): Boolean = {
    manager.update()
  }

  def getMap(): TiledMap = {
    manager.get(MAP_PATH, classOf[TiledMap])
  }

  override def dispose(): Unit = {
    manager.dispose()
  }
  
  //  val grassLayer = map.getLayers.get("grass").asInstanceOf[TiledMapTileLayer]
  //  val roadLayer = map.getLayers.get("road").asInstanceOf[TiledMapTileLayer]
  //  val sandLayer = map.getLayers.get("sand").asInstanceOf[TiledMapTileLayer]
  //  val checkpointLayer = map.getLayers.get("checkpoint").asInstanceOf[TiledMapTileLayer]


  def spawnPlacementForTheCar(): ArrayBuffer[Vector2] = {

    val map: TiledMap = new TmxMapLoader().load(MAP_PATH)
    val finishLayer = map.getLayers.get("finish").asInstanceOf[TiledMapTileLayer]
    val allSpawn: ArrayBuffer[Vector2] = ArrayBuffer.empty

    val mapWidth = finishLayer.getWidth
    val mapHeight = finishLayer.getHeight
    val tileWidth = finishLayer.getTileWidth
    val tileHeight = finishLayer.getTileHeight

    for (x <- 0 until mapWidth; y <- 0 until mapHeight) {
      val cell = finishLayer.getCell(x, y)
      if (cell != null) {

        val position = new Vector2(
          x * tileWidth + tileWidth / 2f,
          y * tileHeight + tileHeight / 2f
        )

        allSpawn.addOne(position)
      }
    }
    allSpawn
  }

  def generateHitBoxes(): ArrayBuffer[PhysicsStaticBox] = {
    val map : TiledMap = new TmxMapLoader().load(MAP_PATH)
    val wallLayer = map.getLayers.get("wall").asInstanceOf[TiledMapTileLayer]
    val allHitBox: ArrayBuffer[PhysicsStaticBox] = ArrayBuffer.empty



    val mapWidth = wallLayer.getWidth
    val mapHeight = wallLayer.getHeight
    val tileWidth = wallLayer.getTileWidth
    val tileHeight = wallLayer.getTileHeight


    for (x <- 0 until mapWidth; y <- 0 until mapHeight) {
      val cell = wallLayer.getCell(x, y)

      val position = new Vector2(
        x * tileWidth + tileWidth / 2f,
        y * tileHeight + tileHeight / 2f
      )

      if (cell != null) {

        val box = new PhysicsStaticBox("", position, tileWidth, tileHeight, math.toRadians(90.0).toFloat)

        allHitBox.addOne(box)
      }
    }
    println(allHitBox.mkString)
    allHitBox
  }

}
