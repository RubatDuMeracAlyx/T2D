package ch.hevs.gdx2d.mygame

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer, TmxMapLoader}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable

import scala.collection.mutable.ArrayBuffer

class Map(val mapName:String) extends Disposable {
  val manager: AssetManager = new AssetManager()

  private val MAP_PATH  = "data/res/"+mapName+".tmx"


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


  def findCheckPointBlocksCoords(): ArrayBuffer[Vector2] = {
    val map: TiledMap = new TmxMapLoader().load(MAP_PATH)
    val checkpointLayer = map.getLayers.get("checkpoint").asInstanceOf[TiledMapTileLayer]
    val CheckPointBlocksCoords: ArrayBuffer[Vector2] = ArrayBuffer.empty

    val mapWidth = checkpointLayer.getWidth
    val mapHeight = checkpointLayer.getHeight

    for (x <- 0 until mapWidth; y <- 0 until mapHeight) {
      val cell = checkpointLayer.getCell(x, y)
      if (cell != null) {

        val position = new Vector2(x , y)

        CheckPointBlocksCoords.append(position)
      }
    }
    CheckPointBlocksCoords
  }

  def existingNeighbors(vec2 : Vector2, listOfVec2 : ArrayBuffer[Vector2]) : ArrayBuffer[Vector2] = {
    var existingNeighbors : ArrayBuffer[Vector2] = ArrayBuffer.empty
    val vec21 : Vector2 = new Vector2(vec2.x+1,vec2.y)
    val vec22 : Vector2 = new Vector2(vec2.x-1,vec2.y)
    val vec23 : Vector2 = new Vector2(vec2.x,vec2.y+1)
    val vec24 : Vector2 = new Vector2(vec2.x,vec2.y-1)

    for (i <- listOfVec2){
      if (i.x == vec21.x && i.y == vec21.y){existingNeighbors.addOne(vec21)}
      if (i.x == vec22.x && i.y == vec22.y){existingNeighbors.addOne(vec22)}
      if (i.x == vec23.x && i.y == vec23.y){existingNeighbors.addOne(vec23)}
      if (i.x == vec24.x && i.y == vec24.y){existingNeighbors.addOne(vec24)}
    }
    existingNeighbors
  }

  def findEqualsIndex(vec : Vector2, arrVec : ArrayBuffer[Vector2]) : Int = {
    val equalsIndex : ArrayBuffer[Int] = ArrayBuffer.empty
    for (i <- arrVec.indices){
      if (arrVec(i).x == vec.x && arrVec(i).y == vec.y){
        return i
      }
    }
    -1
  }

  def findCheckPoints(checkPointBlocks : ArrayBuffer[Vector2]): ArrayBuffer[ArrayBuffer[Vector2]] = {
    val checkpoints : ArrayBuffer[ArrayBuffer[Vector2]] = ArrayBuffer.empty

    while (checkPointBlocks.length != 0) {
      val checkpoint: ArrayBuffer[Vector2] = ArrayBuffer.empty
      checkpoint.addOne(checkPointBlocks(0))
      val firstFoundedBlock: Vector2 = checkPointBlocks(0)
      checkPointBlocks.remove(0)
      val toExplore: ArrayBuffer[Vector2] = ArrayBuffer(firstFoundedBlock)
      while (toExplore.nonEmpty) {
        val current = toExplore(0)
        toExplore.remove(0)
        val neighbors = existingNeighbors(current,checkPointBlocks)
        for (n <- neighbors){
          toExplore.addOne(n)
          checkpoint.addOne(n)
        }
        for (n <- neighbors) {
          val toDelete: Int = findEqualsIndex(n, checkPointBlocks)
          if (toDelete != -1){
            checkPointBlocks.remove(toDelete)
          }
        }
      }
      checkpoints.addOne(checkpoint)
    }

    //createCheckPoints(checkpoints)
    checkpoints
  }

  def createCheckPoints(arr: ArrayBuffer[ArrayBuffer[Vector2]]): Unit = {
    for(i <- 0 until arr.length){
      val name : String = "checkpoint" + (i + 1)
      var cp:Checkpoint = new Checkpoint(arr(i),name)
    }
  }

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

        allSpawn.append(position)
      }
    }
    allSpawn
  }

  def generateHitBoxes(): Unit = {
    val map : TiledMap = new TmxMapLoader().load(MAP_PATH)
    val wallLayer = map.getLayers.get("wall").asInstanceOf[TiledMapTileLayer]

    val mapWidth = wallLayer.getWidth
    val mapHeight = wallLayer.getHeight

    for (x <- 0 until mapWidth; y <- 0 until mapHeight) {
      val cell = wallLayer.getCell(x, y)

      val position = new Vector2(
        x * Map.tileWidth + Map.tileWidth / 2f,
        y * Map.tileHeight + Map.tileHeight / 2f
      )

      // A wall is just a solid static box: Box2D blocks the car against it on
      // its own, so no per-wall contact listener is needed.
      if (cell != null) {
        new Wall(position, Map.tileWidth, Map.tileHeight)
      }
    }
  }

  def generateSand(): Unit = {
    val map: TiledMap = new TmxMapLoader().load(MAP_PATH)
    val sandLayer = map.getLayers.get("sand").asInstanceOf[TiledMapTileLayer]

    if (!sandLayer.isVisible)
      return

    val mapWidth = sandLayer.getWidth
    val mapHeight = sandLayer.getHeight

    var count : Int = 0

    for (x <- 0 until mapWidth; y <- 0 until mapHeight) {
      val cell = sandLayer.getCell(x, y)

      val position = new Vector2(
        x * Map.tileWidth + Map.tileWidth / 2f,
        y * Map.tileHeight + Map.tileHeight / 2f
      )

      if (cell != null) {
        count += 1
        // Sensor: the car drives over the sand (no bounce) but the contact is
        // still reported to GameContactListener, which flips Player.onSand.
        val sand = new Sand(position, Map.tileWidth, Map.tileHeight)
        sand.setSensor(true)
      }


    }
    println(s"number of sand cells ${count}")
    //allSandHitBox
  }

}




object Map {
  val tileWidth = 128
  val tileHeight = 128
}
