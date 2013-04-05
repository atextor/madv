package de.atextor.madv.engine

import org.newdawn.slick.Image
import org.newdawn.slick.ImageBuffer
import org.newdawn.slick.Renderable
import de.atextor.madv.engine.Util.pipelineSyntax
import org.newdawn.slick.Color

class AutoMap(level: Level, player: Player) extends Renderable {
  import level.PlacedLevelCell
  val image = {
    val imageBuffer = new ImageBuffer(level.width, level.height)
    level.placedCells.foreach(pc => imageBuffer.setRGBA(pc.pos.x, pc.pos.y, 0, 0, 0, 0))
    new Image(imageBuffer, Image.FILTER_NEAREST)
  } 
  
  val exit = Color.green
  val islandBorder = Color.black
  val playerColor = Color.red
  val invisible = new Color(0.0f, 0.0f, 0.0f, 0.0f)
  val walkable = new Color(1.0f, 1.0f, 1.0f, 0.6f)
  
  private def cellToPixel(pc: PlacedLevelCell): Color = pc match {
    case c if c.cell.properties contains Exit => exit
    case c if (c + Up).cell.properties contains Exit => exit
    case c if (c + Left).cell.properties contains Exit => exit
    case c if (c + Up + Left).cell.properties contains Exit => exit
    case c if c.cell.properties contains Walkable => walkable
    case c if c.cell.properties contains IslandBorder => islandBorder
    case _ => null  // Faster than creating Some(color) for every pixel
  }
  
  private def renderPixel(pc: PlacedLevelCell) {
    val g = image.getGraphics
    val rgba = cellToPixel(pc)
    if (rgba != null) {
      g.setColor(rgba)
      g.fillRect(pc.pos.x.toFloat, pc.pos.y.toFloat, 1f, 1f)
    }
  }
  
  private def renderPlayer {
    val g = image.getGraphics
    g.setColor(playerColor)
    val p = player.pos / 16
    g.fillRect(p.x, p.y, 2, 2)
  }
  
  def draw(x: Float, y: Float) = image.draw(x, y)
  
  def uncoverMap(player: Player) = {
    level.placedCells.foreach(renderPixel)
    renderPlayer
    image.getGraphics.flush()
  }
  
  def update(playerPos: Vec2d) {
    level.placedCells.filter { pc =>
      val a = pc.pos.x.toInt - (playerPos.x.toInt / 16)
      val b = pc.pos.y.toInt - (playerPos.y.toInt / 16)
      math.sqrt(a * a + b * b) < 10
    } foreach(renderPixel)
    renderPlayer
    image.getGraphics.flush()
  }

}