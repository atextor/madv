package de.atextor.madv.engine

import scala.language.postfixOps
import org.newdawn.slick.Animation
import org.newdawn.slick.Image
import org.newdawn.slick.Renderable
import org.newdawn.slick.SpriteSheet
import scala.util.Random

sealed abstract class CellProperty
case object Walkable extends CellProperty
case object Exit extends CellProperty

case class LevelCell(
  val layer0: Option[Renderable] = None,
  val layer1: Option[Renderable] = None)(
  val properties: CellProperty*) {
  def layer(i: Int) = if (i == 0) layer0 else layer1
}
 
object CaveDefinition {
  val sheet = new SpriteSheet("res/tilesets/cave.png", 16, 16); 
  implicit val defaultCave = GreenCave
}

case class StackedRenderable(r: Renderable*) extends Renderable {
  def draw(x: Float, y: Float) = r foreach(_.draw(x, y))
}

abstract class CaveDefinition {
  val sheet = CaveDefinition.sheet
  def space: Renderable
  def topLeft: Renderable
  def top: Renderable
  def topRight: Renderable
  def left: Renderable
  def right: Renderable
  def bottomLeft: Renderable
  def bottom: Renderable
  def bottomRight: Renderable
  def innerTopLeft: Renderable
  def innerTopRight: Renderable
  def innerBottomLeft: Renderable
  def innerBottomRight: Renderable
  
  private[this] val standardFloors =
    List((1, 1), (0, 9), (1, 9), (0, 10), (1, 10)).map((sheet.getSprite _).tupled(_))
  private[this] val floorVariations =
    List((0, 7), (1, 7), (3, 7), (0, 8), (1, 8), (3, 8), (3, 9), (4, 9)).map((sheet.getSprite _).tupled(_))
  def floor: Renderable = {
    if (Random.nextInt(100) > 5) {
      Random shuffle standardFloors head
    } else {
      Random shuffle floorVariations head
    }
  }
}

case object GreenCave extends CaveDefinition {
  val space = sheet.getSprite(2, 3)
  val topLeft = sheet.getSprite(0, 0)
  val top = sheet.getSprite(1, 0)
  val topRight = sheet.getSprite(2, 0)
  val left = sheet.getSprite(0, 1)
  val right = sheet.getSprite(2, 1)
  val bottomLeft = sheet.getSprite(0, 2)
  val bottom = sheet.getSprite(1, 2)
  val bottomRight = sheet.getSprite(2, 2)
  val innerTopLeft = sheet.getSprite(0, 3)
  val innerTopRight = sheet.getSprite(1, 3)
  val innerBottomLeft = sheet.getSprite(0, 4)
  val innerBottomRight = sheet.getSprite(1, 4)
}

case object LavaCave extends CaveDefinition {
  val space = {
    val ani = new Animation
    for (x <- 0 to 5) {
      ani.addFrame(sheet.getSprite(x, 19), 500)
      ani.addFrame(sheet.getSprite(x, 20), 500)
    }
    ani setPingPong true
    ani
  }
  val topLeft = StackedRenderable(space, sheet.getSprite(0, 11))
  val top = StackedRenderable(space, sheet.getSprite(1, 11))
  val topRight = StackedRenderable(space, sheet.getSprite(2, 11))
  val left = StackedRenderable(space, sheet.getSprite(0, 12))
  val right = StackedRenderable(space, sheet.getSprite(2, 12))
  val bottomLeft = StackedRenderable(space, sheet.getSprite(0, 13))
  val bottom = StackedRenderable(space, sheet.getSprite(1, 13))
  val bottomRight = StackedRenderable(space, sheet.getSprite(2, 13))
  val innerTopLeft = StackedRenderable(space, sheet.getSprite(0, 14))
  val innerTopRight = StackedRenderable(space, sheet.getSprite(1, 14))
  val innerBottomLeft = StackedRenderable(space, sheet.getSprite(0, 15))
  val innerBottomRight = StackedRenderable(space, sheet.getSprite(1, 15))
}

case object BlueCave extends CaveDefinition {
  val space = sheet.getSprite(11, 13)
  val topLeft = sheet.getSprite(6, 13)
  val top = sheet.getSprite(7, 13)
  val topRight = sheet.getSprite(8, 13)
  val left = sheet.getSprite(6, 14)
  val right = sheet.getSprite(8, 14)
  val bottomLeft = sheet.getSprite(6, 15)
  val bottom = sheet.getSprite(7, 15)
  val bottomRight = sheet.getSprite(8, 15)
  val innerTopLeft = sheet.getSprite(9, 13)
  val innerTopRight = sheet.getSprite(10, 13)
  val innerBottomLeft = sheet.getSprite(9, 14)
  val innerBottomRight = sheet.getSprite(10, 14)
}

case object BlackCave extends CaveDefinition {
  val space = sheet.getSprite(8, 8)
  val topLeft = sheet.getSprite(6, 5)
  val top = sheet.getSprite(7, 5)
  val topRight = sheet.getSprite(8, 5)
  val left = sheet.getSprite(6, 6)
  val right = sheet.getSprite(8, 6)
  val bottomLeft = sheet.getSprite(6, 7)
  val bottom = sheet.getSprite(7, 7)
  val bottomRight = sheet.getSprite(8, 7)
  val innerTopLeft = sheet.getSprite(6, 8)
  val innerTopRight = sheet.getSprite(7, 8)
  val innerBottomLeft = sheet.getSprite(6, 9)
  val innerBottomRight = sheet.getSprite(7, 9)
}

case object StairsEntry {
  val sheet = CaveDefinition.sheet
  val row1 = List((12, 3), (13, 3), (14, 3), (13, 3), (15, 3))
  val row2 = List((12, 4), (13, 4), (14, 4), (13, 4), (15, 4))
  val row3 = List((12, 5), (3, 1), (4, 1), (5, 1), (15, 5))
  val row4 = List((12, 6), (3, 2), (4, 2), (5, 2), (15, 6))
  
}
  
object Level {
  // for debugging only
  val scale = 2
  
  def fromCellularAutomaton(ca: CellularAutomaton)(implicit cd: CaveDefinition): Level = {
    val alive = ca.isAlive _
    val dead = ca.isDead _
    val levelCells = ca.allCells.map (_ match {
      case c if alive(c) => LevelCell(layer0 = Some(cd.floor))(Walkable)
      case c if alive(c + Down) && dead(c + Left) && dead(c + Right) => LevelCell(layer0 = Some(cd.top))()
      case c if alive(c + Up) && dead(c + Left) && dead(c + Right)   => LevelCell(layer0 = Some(cd.bottom))()
      case c if alive(c + Right) && dead(c + Up) && dead(c + Down)   => LevelCell(layer0 = Some(cd.left))()
      case c if alive(c + Left) && dead(c + Up) && dead(c + Down)    => LevelCell(layer0 = Some(cd.right))()
      case c if alive(c + Right) && alive(c + Down)                  => LevelCell(layer0 = Some(cd.innerTopLeft))()
      case c if alive(c + Left) && alive(c + Down)                   => LevelCell(layer0 = Some(cd.innerTopRight))()
      case c if alive(c + Right) && alive(c + Up)                    => LevelCell(layer0 = Some(cd.innerBottomLeft))()
      case c if alive(c + Left) && alive(c + Up)                     => LevelCell(layer0 = Some(cd.innerBottomRight))()
      case c if alive(c + DownRight)                                 => LevelCell(layer0 = Some(cd.topLeft))()
      case c if alive(c + DownLeft)                                  => LevelCell(layer0 = Some(cd.topRight))()
      case c if alive(c + UpRight)                                   => LevelCell(layer0 = Some(cd.bottomLeft))()
      case c if alive(c + UpLeft)                                    => LevelCell(layer0 = Some(cd.bottomRight))()
      case c => LevelCell(layer0 = Some(cd.space))()
    })
    Level(width = ca.width, height = ca.height, cells = levelCells)
  }
  
  def placeExit(l: Level): Level = {
    l
  }
}
  
case class Level(width: Int, height: Int, val cells: IndexedSeq[LevelCell]) {
  def at(x: Int, y: Int): LevelCell = cells(x + y * width)
  def at(p: Vec): LevelCell = at((p.x + 8) / 16, (p.y + 8) / 16)
  def indexToVec(index: Int) = Vec2d(index % width, index / width)
  
  def blockToScreenX(x: Int, offsetX: Int) = x * 16 - offsetX + 95
  def blockToScreenY(y: Int, offsetY: Int) = y * 16 - offsetY + 70
  
  
  def draw(offset: Vec2d, layer: Int) {
    val p = Vec2d(offset.x / 16, offset.y / 16)
    val blockOffsetX = (offset.x + 8) / 16
    val blockOffsetY = (offset.y + 8) / 16
    val window = 40 / Level.scale
    for (x <- blockOffsetX - window to blockOffsetX + window;
         y <- blockOffsetY - window to blockOffsetY + window) {
      val tile = if (x > 0 && x < width && y > 0 && y < height) at(x, y).layer(layer) else at(0, 0).layer(layer)
//      tile.foreach(_.draw(x * 16 - offset.x + 150, y * 16 - offset.y + 100))
      tile.foreach(_.draw(blockToScreenX(x, offset.x), blockToScreenY(y, offset.y)))
    }
//    at(0, 0).layer(0).foreach(r => r.draw(blockToScreenX(p.x, offset.x), blockToScreenY(p.y, offset.y)))
  }
}
  