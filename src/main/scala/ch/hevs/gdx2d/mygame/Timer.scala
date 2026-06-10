package ch.hevs.gdx2d.mygame

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.{Color, OrthographicCamera}

class Timer(){
  var startTime = System.currentTimeMillis()
  var timer_finished = false
  var final_time : Double = 0.0
  def getTime():Double = {
    var currentTime:Double = System.currentTimeMillis()-startTime
    currentTime = currentTime/1000
    return currentTime
  }
  def stopTime():Double={
    if(timer_finished==false){
      final_time= getTime()
      timer_finished = true
    }
    return final_time
  }
}
