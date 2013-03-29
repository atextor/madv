package de.atextor.madv.engine

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt

import org.newdawn.slick.Renderable
import org.newdawn.slick.SpriteSheet

abstract class GameItem(name: String, description: String, val effect: Option[GameEffect]) extends Renderable {
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

class Menu(title: String, itemsStackable: Boolean) extends Overlay(pos = Vec2d(100, 50)) with CanGo {
  val size = Vec2d(200, 200)
  val box = new FrameBox(size)
  val text = new Text(title)
  val arrow = SpriteAnimation(new SpriteSheet("res/ui/arrow.png", 16, 7), new SimpleSprite(2, 500 millis), 0).get
  var selection = -1
  active = false
  val sizeMap = scala.collection.mutable.Map[String, Text]()
  
  private val stuff: ListBuffer[GameItem] = new ListBuffer()
  
  def addItem(i: GameItem) = {
    if (stuff.isEmpty) selection = 0
    stuff += i
    val t = sizeMap.getOrElseUpdate(i.toString, new Text("0"))
    t.text = (t.text.toInt + 1).toString
  }
  
  def removeItem(i: GameItem) = {
    stuff -= i
    val t = sizeMap.get(i.toString).get
    t.text = (t.text.toInt - 1).toString
    if (selection > stuff.groupBy(_.toString).size - 1) selection = stuff.groupBy(_.toString).size - 1
    if (stuff.isEmpty) selection = -1
  }
  
  def draw {
    box.draw(pos.x, pos.y)
    text.draw(pos.x + size.x / 2 - text.getWidth / 2, pos.y + 7)
    
    stuff.groupBy(_.toString).map(_._2.head).zipWithIndex.foreach { s =>
      if (itemsStackable) {
        sizeMap.get(s._1.toString).foreach(_.draw(pos.x + 25, pos.y + 20 + s._2 * 10))
        s._1.draw(pos.x + 40, pos.y + 20 + s._2 * 10)
      } else {
        s._1.draw(pos.x + 25, pos.y + 20 + s._2 * 10)
      }
    }
    
    if (!stuff.isEmpty) {
      arrow.draw(pos.x + 7, pos.y + 21 + selection * 10)
      stuff.groupBy(_.toString).toList(selection)._2.head.drawDescription(200, 200)
    }
  }
  
  def go(d: Direction) = d match {
    case Up => {
      if (!stuff.isEmpty) {
        selection -= 1
        if (selection < 0) selection = stuff.groupBy(_.toString).size - 1
      }
    }
    case Down => {
      if (!stuff.isEmpty) {
        selection += 1
        if (selection > stuff.groupBy(_.toString).size - 1) selection = 0
      }
    }
    case _ =>
  }
  
  def activateSelected: Option[GameItem] = {
    val item: Option[GameItem] = if (selection > -1) Some(stuff.groupBy(_.toString).toList(selection)._2.head) else None
    if (itemsStackable) {
      item.foreach(removeItem(_))
    }
    item
  }
}

object Inventory extends Menu("Inventar", itemsStackable = true)
object SpellSelection extends Menu("Zaubersprüche", itemsStackable = false)
