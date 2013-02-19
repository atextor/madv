package de.atextor.madv.engine

import org.newdawn.slick.Image
import org.newdawn.slick.Renderable
import org.newdawn.slick.Color

class Inventory(size: Vec2d) extends Renderable {
  private def image(r: String) = {
    val img = new Image(s"res/ui/box${r}.png")
    img.setFilter(Image.FILTER_NEAREST)
    img
  }
  
  val boxnw = image("nw")
  val boxn = image("n")
  val boxne = image("ne")
  val boxw = image("w")
  val boxc = image("c")
  val boxe = image("e")
  val boxsw = image("sw")
  val boxs = image("s")
  val boxse = image("se")
  
  var active = true
  
  def draw(x: Float, y: Float) {
    if (active) {
      boxnw.draw(x, y)
      boxn.draw(x + boxnw.getWidth, y, size.x - boxnw.getWidth - boxne.getWidth, boxn.getHeight, Color.white)
      boxne.draw(x + size.x - boxne.getWidth, y, boxne.getWidth, boxne.getHeight, Color.white)
      boxw.draw(x, y + boxnw.getHeight, boxw.getWidth, size.y - boxnw.getHeight - boxsw.getHeight, Color.white)
      boxc.draw(x + boxw.getWidth, y + boxn.getHeight, size.x - boxw.getWidth - boxe.getWidth, size.y - boxn.getHeight - boxs.getHeight, Color.white) 
      
    }
  }
}