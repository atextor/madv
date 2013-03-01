package de.atextor.madv.engine

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt

import org.newdawn.slick.Color
import org.newdawn.slick.Image
import org.newdawn.slick.Renderable
import org.newdawn.slick.SpriteSheet

class FrameBox(size: Vec2d) extends Renderable {
  private def image(r: String) = {
    val img = new Image(s"res/ui/box${r}.png")
    img.setFilter(Image.FILTER_NEAREST)
    img
  }
  
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

abstract class InventoryItem(name: String) {
  val text = new Text(name)
}

case class Potion() extends InventoryItem("Potion")

class Inventory extends Overlay(pos = Vec2d(100, 50)) {
  val size = Vec2d(200, 200)
  val box = new FrameBox(size)
  val text = new Text("Inventar")
  val arrow = SpriteAnimation(new SpriteSheet("res/ui/arrow.png", 16, 7), new SimpleSprite(2, 500 millis), 0)
  var selection = -1
  active = false
  
  private val stuff: ListBuffer[InventoryItem] = new ListBuffer()
  
  addItem(Potion())
  addItem(Potion())
  
  def addItem(i: InventoryItem) = {
    if (stuff.isEmpty) selection = 0
    stuff += i
  }
  
  def removeItem(i: InventoryItem) = {
    stuff -= i
    if (selection > stuff.size - 1) selection = stuff.size - 1
    if (stuff.isEmpty) selection = -1
  }
  
  def draw {
    box.draw(pos.x, pos.y)
    text.draw(pos.x + size.x / 2 - text.getWidth / 2, pos.y + 7)
    stuff.zipWithIndex.foreach(s => s._1.text.draw(pos.x + 25, pos.y + 20 + s._2 * 10))
    if (!stuff.isEmpty) arrow.draw(pos.x + 7, pos.y + 21 + selection * 10)
  }
  
  def tick(delta: Int) { }
  
  def changeSelection(d: Direction) = {
    d match {
      case Up => {
        if (!stuff.isEmpty) {
          selection -= 1
          if (selection < 0) selection = stuff.size - 1
        }
      }
      case Down => {
        if (!stuff.isEmpty) {
          selection += 1
          if (selection > stuff.size - 1) selection = 0
        }
      }
      case _ =>
    }
  }
  
  def activateSelected {
    removeItem(stuff(selection))
  }
}