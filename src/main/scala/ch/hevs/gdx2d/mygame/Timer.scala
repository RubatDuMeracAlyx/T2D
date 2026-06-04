package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.{Color, OrthographicCamera}

class Timer(){
  var startTime = System.currentTimeMillis()

  def getTime():Double = {
    var currentTime:Double = System.currentTimeMillis()-startTime
    currentTime = currentTime/1000
    return currentTime
  }
}
