package ch.hevs.gdx2d.mygame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}

import java.util.logging.FileHandler

class MapsManager {

  var map: TiledMap = _
  var mapRenderer: OrthogonalTiledMapRenderer = _

  var mapPixelWidth: Float = 0f
  var mapPixelHeight: Float = 0f

  def load(loadedMap: TiledMap): Unit = {
    this.map = loadedMap
    this.mapRenderer = new OrthogonalTiledMapRenderer(this.map)

    val prop = map.getProperties

    val mapWidthInTiles = prop.get("width", classOf[Int])
    val mapHeightInTiles = prop.get("height", classOf[Int])
    val tileWidth = prop.get("tilewidth", classOf[Int])
    val tileHeight = prop.get("tileheight", classOf[Int])

    mapPixelWidth = (mapWidthInTiles * tileWidth).toFloat
    mapPixelHeight = (mapHeightInTiles * tileHeight).toFloat
  }

  def render(camera: OrthographicCamera): Unit = {
    if (mapRenderer != null) {
      mapRenderer.setView(camera)
      mapRenderer.render()
    }
  }

  def dispose(): Unit = {
    mapRenderer.dispose()
    map.dispose()
  }

  def worldToGrid(pixel: Float): Int = {
    (pixel / 128).toInt
  }
}
