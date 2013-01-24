package de.atextor.madv.engine

import org.newdawn.slick.Image
import org.newdawn.slick.SpriteSheet

case class LevelCell(
  val img: Image,
  val walkable: Boolean)
  
object Level {
  // for debugging only
  val scale = 4
  
  val ss = new SpriteSheet("res/tilesets/cave.png", 16, 16); 
  val space = ss.getSprite(2, 3)
  val tl = ss.getSprite(0, 0)
  val top = ss.getSprite(1, 0)
  val tr = ss.getSprite(2, 0)
  val left = ss.getSprite(0, 1)
  val floor = ss.getSprite(1, 1)
  val right = ss.getSprite(2, 1)
  val bl = ss.getSprite(0, 2)
  val bottom = ss.getSprite(1, 2)
  val br = ss.getSprite(2, 2)
  val innertl = ss.getSprite(0, 3)
  val innertr = ss.getSprite(1, 3)
  val innerbl = ss.getSprite(0, 4)
  val innerbr = ss.getSprite(1, 4)
  
  def potholes(ca: CellularAutomaton): Set[Cell] = ca.allCells.collect(_ match {
    case c if ca.isDead(c) && ca.isAlive(c + Up) && ca.isAlive(c + Down) => c
    case c if ca.isDead(c) && ca.isAlive(c + Left) && ca.isAlive(c + Right) => c
  }).toSet
  
  private def fixSingleCells(ca: CellularAutomaton): CellularAutomaton = {
    val alive = ca.isAlive _
    val dead = ca.isDead _
    val fixHoles = (ca: CellularAutomaton) => ca.copy(liveCells = ca.liveCells ++ potholes(ca))
    lazy val lvl: Stream[CellularAutomaton] = ca #:: lvl.map(fixHoles(_))
    lvl.find(potholes(_).size == 0).get
  }
  
  def fromCellularAutomaton(ca: CellularAutomaton): Level = {
    val preprocCA = fixSingleCells(ca)
    val alive = preprocCA.isAlive _
    val dead = preprocCA.isDead _
    val levelCells = preprocCA.allCells.map (_ match {
      case c if alive(c) => LevelCell(floor, walkable = true)
      case c if alive(c + Down) && dead(c + Left) && dead(c + Right) => LevelCell(top, walkable = false)
      case c if alive(c + Up) && dead(c + Left) && dead(c + Right)   => LevelCell(bottom, walkable = false)
      case c if alive(c + Right) && dead(c + Up) && dead(c + Down)   => LevelCell(left, walkable = false)
      case c if alive(c + Left) && dead(c + Up) && dead(c + Down)    => LevelCell(right, walkable = false)
      case c if alive(c + Right) && alive(c + Down)                  => LevelCell(innertl, walkable = false)
      case c if alive(c + Left) && alive(c + Down)                   => LevelCell(innertr, walkable = false)
      case c if alive(c + Right) && alive(c + Up)                    => LevelCell(innerbl, walkable = false)
      case c if alive(c + Left) && alive(c + Up)                     => LevelCell(innerbr, walkable = false)
      case c if alive(c + DownRight)                                 => LevelCell(tl, walkable = false)
      case c if alive(c + DownLeft)                                  => LevelCell(tr, walkable = false)
      case c if alive(c + UpRight)                                   => LevelCell(bl, walkable = false)
      case c if alive(c + UpLeft)                                    => LevelCell(br, walkable = false)
      case c => LevelCell(space, walkable = false)
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
      tile.img.draw(x * 17 - offset.x + 150, y * 17 - offset.y + 100)
    }
  }
}
  