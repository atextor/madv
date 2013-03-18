package de.atextor.madv.engine

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import org.newdawn.slick.Renderable
import org.newdawn.slick.SpriteSheet

abstract class InventoryItem(name: String, description: String, val effect: GameEffect) extends Renderable {
  val text = new Text(name)
  val desc = new Text(description)
  val tbox = new TextBox(180, description, Vec2d(300, 100))
  
  def draw(x: Float, y: Float) {
    text.draw(x, y)
  }
  
  def drawDescription(x: Float, y: Float) {
    tbox.pos = Vec2d(x.toInt, y.toInt)
    tbox.draw
  }
}

case class Potion() extends InventoryItem("Potion", "A strange potion.\nWill restore 100 HP.", PlayerHealth)
case class MagicMapScroll() extends InventoryItem("Magic Map Scroll",
    "This scroll will uncover\nthe whole map.", MagicMapping)

class Inventory extends Overlay(pos = Vec2d(100, 50)) {
  val size = Vec2d(200, 200)
  val box = new FrameBox(size)
  val text = new Text("Inventar")
  val arrow = SpriteAnimation(new SpriteSheet("res/ui/arrow.png", 16, 7), new SimpleSprite(2, 500 millis), 0).get
  var selection = -1
  active = false
  
  private val stuff: ListBuffer[InventoryItem] = new ListBuffer()
  
  addItem(Potion())
  addItem(Potion())
  addItem(MagicMapScroll())
  
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
    stuff.zipWithIndex.foreach(s => s._1.draw(pos.x + 25, pos.y + 20 + s._2 * 10))
    if (!stuff.isEmpty) {
      arrow.draw(pos.x + 7, pos.y + 21 + selection * 10)
      stuff(selection).drawDescription(200, 200)
    }
  }
  
  def changeSelection(d: Direction) = d match {
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
  
  def activateSelected: Option[InventoryItem] = {
    val item: Option[InventoryItem] = if (selection > -1) Some(stuff(selection)) else None
    item.foreach(removeItem(_))
    item
  }
}