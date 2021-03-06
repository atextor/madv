package de.atextor.madv.engine

import org.newdawn.slick.Color
import org.newdawn.slick.Image
import org.newdawn.slick.Renderable

object UI {
  def image(res: String) = {
    val img = new Image(res)
    img.setFilter(Image.FILTER_NEAREST)
    img
  }
}

abstract class Overlay(var pos: Vec2d) extends Tickable {
  def draw
  def tick(scene: Scene, delta: Int) {}
  var alive = true
  var active = true
}

class HealthDisplay(player: Player) extends Overlay(pos = Vec2d(180, 2)) {
  val heart = UI.image("res/ui/heart.png")
  override def draw {
    for (i <- 0 to player.hp / 10) {
      heart.draw(pos.x + i * 10, pos.y)
    }
  }
}

class GoldDisplay(player: Player) extends Overlay(pos = Vec2d(30, 2)) {
  val text = new Text("0")
  
  override def draw {
    text.text = player.gold.toString + " Gold"
    text.showAll 
    text.draw(pos.x, pos.y)
  }
}

class TextBox(width: Int, text: String, startPos: Vec2d) extends Overlay(pos = startPos) {
  val size = Vec2d(width, Text.getTextHeight(text) + 15)
  val box = new FrameBox(size)
  val txt = new Text(text)
  val tw = Text.getTextWidth(text)
  
  override def draw {
    box.draw(pos.x, pos.y)
    txt.draw(pos.x + width / 2 - tw / 2, pos.y + 7)
  }
}

class FrameBox(size: Vec2d) extends Renderable {
  private def image(r: String) = UI.image(s"res/ui/box${r}.png")
  val boxnw = image("nw")
  val boxn  = image("n")
  val boxne = image("ne")
  val boxw  = image("w")
  val boxc  = image("c")
  val boxe  = image("e")
  val boxsw = image("sw")
  val boxs  = image("s")
  val boxse = image("se")
  
  var active = true
  
  def draw(x: Float, y: Float) {
    if (active) {
      boxnw.draw(x, y)
      boxn.draw(x + boxnw.getWidth, y, size.x - boxnw.getWidth - boxne.getWidth, boxn.getHeight, Color.white)
      boxne.draw(x + size.x - boxne.getWidth, y, boxne.getWidth, boxne.getHeight, Color.white)
      boxw.draw(x, y + boxnw.getHeight, boxw.getWidth, size.y - boxnw.getHeight - boxsw.getHeight, Color.white)
      boxc.draw(x + boxw.getWidth, y + boxn.getHeight, size.x - boxw.getWidth - boxe.getWidth, size.y - boxn.getHeight - boxs.getHeight, Color.white) 
      boxe.draw(x + size.x - boxe.getWidth, y + boxne.getHeight, boxe.getWidth, size.y - boxne.getHeight - boxse.getHeight, Color.white)
      boxsw.draw(x, y + size.y - boxsw.getHeight)
      boxs.draw(x + boxsw.getWidth, y + size.y - boxs.getHeight, size.x - boxsw.getWidth - boxse.getWidth, boxs.getHeight, Color.white)
      boxse.draw(x + size.x - boxse.getWidth, y + size.y - boxse.getHeight)
    }
  }
}

class StoryText(storyText: String, portrait: Option[Renderable], onClose: () => Unit = DoNothing) extends Overlay(pos = Vec2d(80, 200)) {
  val size = Vec2d(240, 80)
  val box = new FrameBox(size)
  val text = new Text(storyText, appear = true)
  
  def draw {
    box.draw(pos.x, pos.y)
    text.draw(pos.x + 60, pos.y + 7)
    portrait.foreach(_.draw(pos.x + 10, pos.y + 20))
  } 
  
  override def tick(scene: Scene, delta: Int) {
    text.tick(scene, delta)
  }
  
  def trigger {
    if (text.allShown) {
      alive = false
      onClose()
    } else {
      text.showAll
    }
  }
}
