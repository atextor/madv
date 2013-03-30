package de.atextor.madv.engine

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt
import org.newdawn.slick.Renderable
import org.newdawn.slick.SpriteSheet
import de.atextor.madv.game.Muffin
import de.atextor.madv.game.GameEffect

sealed trait GameItemType
case object SpellGameItem extends GameItemType
case object ItemGameItem extends GameItemType

abstract class GameItem(val itemType: GameItemType, val name: String, description: String, val price: Int, val effect: Option[GameEffect]) extends Renderable {
  val text = new Text(name)
  val priceText = new Text(price.toString)
  val desc = new Text(description)
  val tbox = new TextBox(180, description, Vec2d(300, 100))
  
  def draw(x: Float, y: Float) {
    text.draw(x, y)
  }
  
  def drawPrice(x: Float, y: Float) {
    priceText.draw(x, y)
  }
  
  def drawDescription(x: Float, y: Float) {
    tbox.pos = Vec2d(x.toInt, y.toInt)
    tbox.draw
  }
}

abstract class Menu(title: String, val itemsStackable: Boolean, drawPrices: Boolean = false) extends Overlay(pos = Vec2d(100, 50)) with CanGo {
  val size = Vec2d(200, 200)
  val box = new FrameBox(size)
  val text = new Text(title)
  val arrow = SpriteAnimation(new SpriteSheet("res/ui/arrow.png", 16, 7), new SimpleSprite(2, 500 millis), 0).get
  var selection = -1
  active = false
  val sizeMap = scala.collection.mutable.Map[String, Text]()
  val stuff: ListBuffer[GameItem] = new ListBuffer()
  
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
        if (drawPrices) {
          s._1.drawPrice(pos.x + size.x - 30, pos.y + 20 + s._2 * 10)
        }
      } else {
        s._1.draw(pos.x + 25, pos.y + 20 + s._2 * 10)
        if (drawPrices) {
          s._1.drawPrice(pos.x + size.x - 30, pos.y + 20 + s._2 * 10)
        }
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
  
  def activate(player: Player, processItem: GameItem => Unit)
}

object Inventory extends Menu("Inventar", itemsStackable = true) {
  def activate(player: Player, processItem: GameItem => Unit) {
    val item: Option[GameItem] = if (selection > -1) Some(stuff.groupBy(_.toString).toList(selection)._2.head) else None
    item.foreach { i =>
      if (i != Muffin()) {
        removeItem(i)
      }
      processItem(i)
    }
  }
}

object SpellSelection extends Menu("Zaubersprüche", itemsStackable = false) {
  def activate(player: Player, processItem: GameItem => Unit) {
    val item = if (selection > -1) Some(stuff.groupBy(_.toString).toList(selection)._2.head) else None
    item.foreach(processItem(_))
  }
}

object Shop extends Menu("Muffin Shop", itemsStackable = false, drawPrices = true) {
  def activate(player: Player, processItem: GameItem => Unit) {
    val item: Option[GameItem] = if (selection > -1) Some(stuff.groupBy(_.toString).toList(selection)._2.head) else None
    item.foreach { i =>
      if (i.price <= player.gold || Constants.debug) {
        player.gold -= i.price
        Audio.cashregister.play
        if (i.itemType == SpellGameItem) {
          SpellSelection.addItem(i)
        } else if (i.itemType == ItemGameItem) {
          Inventory.addItem(i)
        }
      } else {
        Audio.bad.play
      }
    }
  }
}
