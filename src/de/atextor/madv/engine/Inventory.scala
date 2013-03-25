package de.atextor.madv.engine

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import org.newdawn.slick.Renderable
import org.newdawn.slick.SpriteSheet

abstract class InventoryItem(name: String, description: String, val effect: GameEffect) extends MenuItem(name, description)

case class Potion() extends InventoryItem("Potion", "A strange potion.\nWill restore 100 HP.", PlayerHealth)
case class MagicMapScroll() extends InventoryItem("Magic Map Scroll",
    "This scroll will uncover\nthe whole map.", MagicMapping)

class Inventory extends Menu[InventoryItem]("Inventar")
