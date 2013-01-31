package de.atextor.madv.engine

import scala.language.postfixOps
import org.newdawn.slick.Animation
import org.newdawn.slick.Image
import org.newdawn.slick.Renderable
import org.newdawn.slick.SpriteSheet
import scala.util.Random

case class LevelCell(
  val visual: Renderable,
  val walkable: Boolean)
  
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
  
object Level {
  // for debugging only
  val scale = 4
  
  def fixPotholes(ca: CellularAutomaton): CellularAutomaton = {
    lazy val fixedCa: Stream[CellularAutomaton] =
      ca #:: fixedCa.map(ca => ca.copy(liveCells = ca.liveCells ++ ca.potholes))
    fixedCa find(_.potholes.size == 0) get
  }
  
  def fromCellularAutomaton(ca: CellularAutomaton)(implicit cd: CaveDefinition): Level = {
//    val fixed = fixPotholes(ca)
    val alive = ca.isAlive _
    val dead = ca.isDead _
    val levelCells = ca.allCells.map (_ match {
      case c if alive(c) => LevelCell(cd.floor, walkable = true)
      case c if alive(c + Down) && dead(c + Left) && dead(c + Right) => LevelCell(cd.top, walkable = false)
      case c if alive(c + Up) && dead(c + Left) && dead(c + Right)   => LevelCell(cd.bottom, walkable = false)
      case c if alive(c + Right) && dead(c + Up) && dead(c + Down)   => LevelCell(cd.left, walkable = false)
      case c if alive(c + Left) && dead(c + Up) && dead(c + Down)    => LevelCell(cd.right, walkable = false)
      case c if alive(c + Right) && alive(c + Down)                  => LevelCell(cd.innerTopLeft, walkable = false)
      case c if alive(c + Left) && alive(c + Down)                   => LevelCell(cd.innerTopRight, walkable = false)
      case c if alive(c + Right) && alive(c + Up)                    => LevelCell(cd.innerBottomLeft, walkable = false)
      case c if alive(c + Left) && alive(c + Up)                     => LevelCell(cd.innerBottomRight, walkable = false)
      case c if alive(c + DownRight)                                 => LevelCell(cd.topLeft, walkable = false)
      case c if alive(c + DownLeft)                                  => LevelCell(cd.topRight, walkable = false)
      case c if alive(c + UpRight)                                   => LevelCell(cd.bottomLeft, walkable = false)
      case c if alive(c + UpLeft)                                    => LevelCell(cd.bottomRight, walkable = false)
      case c => LevelCell(cd.space, walkable = false)
    })
    Level(width = ca.width, height = ca.height, cells = levelCells)
  }
}
  
case class Level(width: Int, height: Int, cells: IndexedSeq[LevelCell]) {
  def at(x: Int, y: Int) = cells(x + y * width)
  def draw(offset: Vec2d){
    val blockOffsetX = (offset.x + 8) / 16
    val blockOffsetY = (offset.y + 8) / 16
    val window = 40 / Level.scale
    for (x <- blockOffsetX - window to blockOffsetX + window;
         y <- blockOffsetY - window to blockOffsetY + window) {
      val tile = if (x > 0 && x < width && y > 0 && y < height) at(x, y) else at(0, 0)
      tile.visual.draw(x * 16 - offset.x + 150, y * 16 - offset.y + 100)
    }
  }
}
  