package de.atextor.madv.engine

import scala.util.Random

import org.newdawn.slick.Animation
import org.newdawn.slick.Image
import org.newdawn.slick.Renderable
import org.newdawn.slick.SpriteSheet
import org.newdawn.slick.util.Log

import de.atextor.madv.engine.Util.pipelineSyntax

sealed abstract class CellProperty
case object Walkable extends CellProperty
case object Exit extends CellProperty
case object IslandBorder extends CellProperty

sealed abstract class LevelSetting(val caveDef: CaveDefinition, val island: Boolean, val hasExit: Boolean = true)
case object CoherentBlue extends LevelSetting(BlueCave, island = false)
case object CoherentGreen extends LevelSetting(GreenCave, island = false)
case object CoherentLava extends LevelSetting(LavaCave, island = false)
case object IslandBlue extends LevelSetting(BlueCave, island = true)
case object IslandGreen extends LevelSetting(GreenCave, island = true)
case object IslandLava extends LevelSetting(LavaCave, island = true)
case object IslandBlack extends LevelSetting(BlackCave, island = true, hasExit = false)

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

trait SpriteSheetHelper {
  val sheet = CaveDefinition.sheet
  val toSprite: ((Int, Int)) => Image = (sheet.getSprite _).tupled(_)
}

abstract class CaveDefinition extends SpriteSheetHelper {
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
  
  private[this] val standardFloors = List((1, 1), (0, 9), (1, 9), (0, 10), (1, 10)) map toSprite
  private[this] val floorVariations = List((0, 7), (1, 7), (3, 7), (0, 8), (1, 8), (3, 8), (3, 9), (4, 9)) map toSprite
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

case object StairsEntry extends SpriteSheetHelper {
  val row1 = List((12, 3), (13, 3), (14, 3), (13, 3), (15, 3)) map toSprite
  val row2 = List((12, 4), (13, 4), (14, 4), (13, 4), (15, 4)) map toSprite
  val row3 = List((12, 5), (3, 1), (4, 1), (5, 1), (15, 5)) map toSprite
  val row4 = List((12, 2), (3, 2), (4, 2), (5, 2), (15, 2)) map toSprite
}
  
object Level {
  val scale = 1//if (Constants.debug) 2 else 1
  
  private def cellularAutomatonToLevel(ca: CellularAutomaton)(implicit cd: CaveDefinition): Level = {
    val alive = ca.isAlive _
    val dead = ca.isDead _
    val levelCells = ca.allCells.map (_ match {
      case c if alive(c) => LevelCell(layer0 = Some(cd.floor))(Walkable)
      case c if alive(c + Down) && dead(c + Left) && dead(c + Right) => LevelCell(layer0 = Some(cd.top))(IslandBorder)
      case c if alive(c + Up) && dead(c + Left) && dead(c + Right)   => LevelCell(layer0 = Some(cd.bottom))(IslandBorder)
      case c if alive(c + Right) && dead(c + Up) && dead(c + Down)   => LevelCell(layer0 = Some(cd.left))(IslandBorder)
      case c if alive(c + Left) && dead(c + Up) && dead(c + Down)    => LevelCell(layer0 = Some(cd.right))(IslandBorder)
      case c if alive(c + Right) && alive(c + Down)                  => LevelCell(layer0 = Some(cd.innerTopLeft))(IslandBorder)
      case c if alive(c + Left) && alive(c + Down)                   => LevelCell(layer0 = Some(cd.innerTopRight))(IslandBorder)
      case c if alive(c + Right) && alive(c + Up)                    => LevelCell(layer0 = Some(cd.innerBottomLeft))(IslandBorder)
      case c if alive(c + Left) && alive(c + Up)                     => LevelCell(layer0 = Some(cd.innerBottomRight))(IslandBorder)
      case c if alive(c + Down + Right)                              => LevelCell(layer0 = Some(cd.topLeft))(IslandBorder)
      case c if alive(c + Down + Left)                               => LevelCell(layer0 = Some(cd.topRight))(IslandBorder)
      case c if alive(c + Up + Right)                                => LevelCell(layer0 = Some(cd.bottomLeft))(IslandBorder)
      case c if alive(c + Up + Left)                                 => LevelCell(layer0 = Some(cd.bottomRight))(IslandBorder)
      case c => LevelCell(layer0 = Some(cd.space))()
    })
    Level(width = ca.width, height = ca.height, cells = levelCells)
  }
  
  private def placeExit[L <: Level](l: L): Level = {
    import l.PlacedLevelCell
    val exitLocationProperty: PlacedLevelCell => Boolean = { c =>
      c.pos.x > 3 && c.pos.y > 3 &&
      (c.cell.properties.contains(Walkable)) &&
      ((c + Left).cell.properties contains Walkable) &&
      ((c + Left * 2).cell.properties contains Walkable) &&
      ((c + Right).cell.properties contains Walkable) &&
      ((c + Right * 2).cell.properties contains Walkable) &&
      ((c + Down).cell.properties contains Walkable) &&
      !((c + Up + Left).cell.properties contains Walkable) &&
      !((c + Up + Left * 2).cell.properties contains Walkable) &&
      !((c + Up).cell.properties contains Walkable) &&
      !((c + Up + Right).cell.properties contains Walkable) &&
      !((c + Up + Right * 2).cell.properties contains Walkable)
    }
    
    // In rare cases, no optimal exit location can be found. In that case,
    // we take the fallback one which can cause slight graphical glitches.
    lazy val exitLocationFallbackProperty: PlacedLevelCell => Boolean = { c =>
      c.pos.x > 3 && c.pos.y > 3 &&
      (c.cell.properties.contains(Walkable)) &&
      ((c + Left).cell.properties contains Walkable) &&
      ((c + Right).cell.properties contains Walkable) &&
      ((c + Down).cell.properties contains Walkable)
    } 
    val exitLocation = l.find(exitLocationProperty).getOrElse {
      Log.debug("Fallback exit location")
      l.find(exitLocationFallbackProperty).get
    }
    Log.debug("Placed Exit: " + exitLocation.pos)
    
    val updatedCells = l.placedCells.map { _ match {
      case p if p.pos == exitLocation.pos + Left * 2 =>
        p.copy(cell = LevelCell(layer0 = Some(StairsEntry.row4(0)))())
      case p if p.pos == exitLocation.pos + Left =>
        p.copy(cell = LevelCell(layer0 = Some(StairsEntry.row4(1)))())
      case p if p.pos == exitLocation.pos =>
        p.copy(cell = LevelCell(layer0 = Some(StairsEntry.row4(2)))(Exit, Walkable))
      case p if p.pos == exitLocation.pos + Right =>
        p.copy(cell = LevelCell(layer0 = Some(StairsEntry.row4(3)))()) 
      case p if p.pos == exitLocation.pos + Right * 2 =>
        p.copy(cell = LevelCell(layer0 = Some(StairsEntry.row4(4)))())
      case p if p.pos == exitLocation.pos + Up + Left * 2 =>
        p.copy(cell = LevelCell(layer0 = Some(StackedRenderable(p.cell.layer0.get, StairsEntry.row3(0))))())
      case p if p.pos == exitLocation.pos + Up + Left =>
        p.copy(cell = LevelCell(layer0 = Some(StackedRenderable(p.cell.layer0.get, StairsEntry.row3(1))))())
      case p if p.pos == exitLocation.pos + Up =>
        p.copy(cell = LevelCell(layer0 = p.cell.layer0, layer1 = Some(StairsEntry.row3(2)))())
      case p if p.pos == exitLocation.pos + Up + Right =>
        p.copy(cell = LevelCell(layer0 = Some(StackedRenderable(p.cell.layer0.get, StairsEntry.row3(3))))())
      case p if p.pos == exitLocation.pos + Up + Right * 2 =>
        p.copy(cell = LevelCell(layer0 = Some(StackedRenderable(p.cell.layer0.get, StairsEntry.row3(4))))())
      case p if p.pos == exitLocation.pos + Up * 2 + Left * 2 =>
        p.copy(cell = LevelCell(layer0 = p.cell.layer0, layer1 = Some(StairsEntry.row2(0)))())
      case p if p.pos == exitLocation.pos + Up * 2 + Left =>
        p.copy(cell = LevelCell(layer0 = p.cell.layer0, layer1 = Some(StairsEntry.row2(1)))())
      case p if p.pos == exitLocation.pos + Up * 2 =>
        p.copy(cell = LevelCell(layer0 = p.cell.layer0, layer1 = Some(StairsEntry.row2(2)))())
      case p if p.pos == exitLocation.pos + Up * 2 + Right =>
        p.copy(cell = LevelCell(layer0 = p.cell.layer0, layer1 = Some(StairsEntry.row2(3)))())
      case p if p.pos == exitLocation.pos + Up * 2 + Right * 2 =>
        p.copy(cell = LevelCell(layer0 = p.cell.layer0, layer1 = Some(StairsEntry.row2(3)))())
      case p if p.pos == exitLocation.pos + Up * 3 + Left * 2 =>
        p.copy(cell = LevelCell(layer0 = p.cell.layer0, layer1 = Some(StairsEntry.row1(0)))())
      case p if p.pos == exitLocation.pos + Up * 3 + Left =>
        p.copy(cell = LevelCell(layer0 = p.cell.layer0, layer1 = Some(StairsEntry.row1(1)))())
      case p if p.pos == exitLocation.pos + Up * 3 =>
        p.copy(cell = LevelCell(layer0 = p.cell.layer0, layer1 = Some(StairsEntry.row1(2)))())
      case p if p.pos == exitLocation.pos + Up * 3 + Right =>
        p.copy(cell = LevelCell(layer0 = p.cell.layer0, layer1 = Some(StairsEntry.row1(3)))())
      case p if p.pos == exitLocation.pos + Up * 3 + Right * 2 =>
        p.copy(cell = LevelCell(layer0 = p.cell.layer0, layer1 = Some(StairsEntry.row1(4)))())
      case p => p
    }}.map(_.cell)
    l.copy(cells = updatedCells, exitLocation = exitLocation.pos * 16)
  }
  
  val cave = new CellularAutomaton.Rule(born = Set(6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
  val smooth = new CellularAutomaton.Rule(born = Set(5, 6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
  
  def generateCoherentLevel(implicit cd: CaveDefinition, withExit: Boolean = true) = {
    CellularAutomaton(40, 40).randomFill(0.4).upscale(smooth)(smooth)(smooth).addDeadBorder.fixPotholes.
      |> (ca => ca.copy(liveCells = ca.sortAreasBySize(ca.areas).last)).
      |> (cellularAutomatonToLevel(_)).
      |> (l => if (withExit) placeExit(l) else l)
  }
  
  def generateIslandLevel(implicit cd: CaveDefinition, withExit: Boolean = true) = {
    CellularAutomaton(50, 50).randomFill(0.5)(cave)(smooth)(smooth)(smooth).upscale(smooth).addDeadBorder.fixPotholes.
      |> (cellularAutomatonToLevel(_)).
      |> (l => if (withExit) placeExit(l) else l)
  }
  
  def generateStaticSmallLevel(implicit cd: CaveDefinition) = {
    val allCells = CellularAutomaton(20, 20).allCells.toSet
    val island = ((for (x <- 0 until 20; y <- 0 until 20) yield (x, y)) collect {
      case (x, y) if (math.sqrt((x - 10) * (x - 10) + (y - 10) * (y - 10)) > 6) => Cell(x, y)
    }).toSet
    CellularAutomaton(width = 20, height = 20, liveCells = allCells -- island).
      |> (cellularAutomatonToLevel(_))
  }
}
  
case class Level(width: Int, height: Int, cells: IndexedSeq[LevelCell], exitLocation: Vec2d = Vec2d(0, 0)) {
  lazy val placedCells = cells.zipWithIndex.map(c => PlacedLevelCell(indexToVec(c._2), c._1))
  
  // Path dependent type, as placed cells depend on this level's cells collection
  case class PlacedLevelCell(pos: Vec2d, cell: LevelCell) {
    def +(d: Vec[Int]) = {
      val coord = pos + d
      PlacedLevelCell(coord, at(coord.x, coord.y))
    }
  }
  
  def find(p: PlacedLevelCell => Boolean, randomize: Boolean = true): Option[PlacedLevelCell] =
    (if (randomize) (Random shuffle placedCells) else placedCells).find(p)
  
  def cellAt(v: Vec[Int]) = at((v.x + 12) / 16, (v.y + 8) / 16)
  
  private def at(x: Int, y: Int) =
    if (x >= 0 && x < width && y >= 0 && y < height) cells(x + y * width) else cells(0)
  private def indexToVec(index: Int) = Vec2d(index % width, index / width)
  
  def draw(offset: Vec2d, layer: Int) {
    val blockOffsetX = (offset.x) / 16
    val blockOffsetY = (offset.y) / 16
    val w = 7 * Level.scale
    for (x <- blockOffsetX - w to blockOffsetX + w; y <- blockOffsetY - w to blockOffsetY + w) {
      at(x, y).layer(layer).foreach(_.draw(x * 16 - offset.x + 90, y * 16 - offset.y + 60))
    }
  }
}
  