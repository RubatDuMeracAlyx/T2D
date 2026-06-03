package ch.hevs.gdx2d.mygame

class Timer {
  var startTime = System.currentTimeMillis()

  def getTime():Double = {
    var currentTime:Double = System.currentTimeMillis()-startTime
    currentTime = currentTime/1000
    return currentTime
  }
}
