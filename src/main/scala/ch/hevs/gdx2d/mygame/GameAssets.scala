package ch.hevs.gdx2d.mygame

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.{TiledMap, TmxMapLoader}
import com.badlogic.gdx.utils.Disposable

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
}
