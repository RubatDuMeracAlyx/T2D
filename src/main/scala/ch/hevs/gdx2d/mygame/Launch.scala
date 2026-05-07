package ch.hevs.gdx2d.hello

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.desktop.DesktopApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.{Color, Texture}


class Launch extends DesktopApplication(1920, 1080) {


  protected var counter: Float = 0f
  protected var imgBitmap: BitmapImage = _

  override def onInit(): Unit = {
    setTitle("Simple image drawing, mui 2013")
    imgBitmap = new BitmapImage("src/main/scala/ch/hevs/gdx2d/mygame/res/image.png")
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.drawBackground(imgBitmap,0,0)


  }
}

object HelloGdx2d {
  def main(args: Array[String]): Unit = {
    new Launch().launch()
  }
}
