package de.atextor.madv.engine

import org.newdawn.slick.Image
import org.newdawn.slick.SpriteSheet

case class LevelCell(val img: Image)
  
object Level {
  // for debugging only
  val scale = 2
  
  val ss = new SpriteSheet(s"res/tilesets/cave.png", 16, 16); 
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
  
  def fromCellularAutomaton(ca: CellularAutomaton): Level =
    Level(width = ca.width, height = ca.height, cells = ca.allCells.map { c =>
      if (ca.isAlive(c)) LevelCell(floor)
      else if (ca.isAlive(c + Down) && ca.isDead(c + Left) && ca.isDead(c + Right)) LevelCell(top)
      else if (ca.isAlive(c + Up) && ca.isDead(c + Left) && ca.isDead(c + Right)) LevelCell(bottom)
      else if (ca.isAlive(c + Right) && ca.isDead(c + Up) && ca.isDead(c + Down)) LevelCell(left)
      else if (ca.isAlive(c + Left) && ca.isDead(c + Up) && ca.isDead(c + Down)) LevelCell(right)
      else if (ca.isAlive(c + Right) && ca.isAlive(c + Down)) LevelCell(innertl)
      else if (ca.isAlive(c + Left) && ca.isAlive(c + Down)) LevelCell(innertr)
      else if (ca.isAlive(c + Right) && ca.isAlive(c + Up)) LevelCell(innerbl)
      else if (ca.isAlive(c + Left) && ca.isAlive(c + Up)) LevelCell(innerbr)
      else if (ca.isAlive(c + Vec2d(1, 1))) LevelCell(tl)
      else if (ca.isAlive(c + Vec2d(-1, 1))) LevelCell(tr)
      else if (ca.isAlive(c + Vec2d(1, -1))) LevelCell(bl)
      else if (ca.isAlive(c + Vec2d(-1, -1))) LevelCell(br)
      else LevelCell(space)
  })
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
      tile.img.draw(x * 16 - offset.x + 150, y * 16 - offset.y + 100)
    }
  }
}
  