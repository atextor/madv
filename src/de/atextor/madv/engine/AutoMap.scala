package de.atextor.madv.engine

import org.newdawn.slick.Image
import org.newdawn.slick.ImageBuffer
import org.newdawn.slick.Renderable

import de.atextor.madv.engine.Util.pipelineSyntax

class AutoMap(level: Level, player: Player) extends Renderable {
  import level.PlacedLevelCell
  case class RGBA(r: Int, g: Int, b: Int, a: Int)
  val imageBuffer = new ImageBuffer(level.width, level.height)
  val image = new Image(imageBuffer, Image.FILTER_NEAREST)
  val invis = RGBA(0, 0, 0, 0)
//  update(player)
  init
  
  private def init {
    level.placedCells.foreach { pc =>
      val rgba = cellToPixel(pc)
      imageBuffer.setRGBA(pc.pos.x, pc.pos.y, invis.r, invis.g, invis.b, invis.a)
    }
  }
  
  private def setLargePixel(pos: Vec2d, rgba: RGBA) {
    imageBuffer.setRGBA(pos.x, pos.y, rgba.r, rgba.g, rgba.b, rgba.a)
    imageBuffer.setRGBA(pos.x + 1, pos.y, rgba.r, rgba.g, rgba.b, rgba.a)
    imageBuffer.setRGBA(pos.x, pos.y + 1, rgba.r, rgba.g, rgba.b, rgba.a)
    imageBuffer.setRGBA(pos.x + 1, pos.y + 1, rgba.r, rgba.g, rgba.b, rgba.a)
  }
  
  private def cellToPixel(pc: PlacedLevelCell): RGBA = pc match {
    case c if c.cell.properties contains Exit => RGBA(0, 255, 0, 255)
    case c if (c + Up).cell.properties contains Exit => RGBA(0, 255, 0, 255)
    case c if (c + Left).cell.properties contains Exit => RGBA(0, 255, 0, 255)
    case c if (c + Up + Left).cell.properties contains Exit => RGBA(0, 255, 0, 255)
    case c if c.cell.properties contains Walkable => RGBA(255, 255, 255, 150)
    case c if c.cell.properties contains IslandBorder => RGBA(0, 0, 0, 255)
    case _ => RGBA(0, 0, 0, 0)
  }
  
  def draw(x: Float, y: Float) = new Image(imageBuffer, Image.FILTER_NEAREST).draw(x, y)
  
  def uncoverMap(player: Player) = {
    level.placedCells.foreach { pc =>
      val rgba = cellToPixel(pc)
      imageBuffer.setRGBA(pc.pos.x, pc.pos.y, rgba.r, rgba.g, rgba.b, rgba.a)
    }
    // Add player
    setLargePixel(player.pos.toVec2d / 16, RGBA(255, 0, 0, 255))
  }
  
  def update(p: Player) {
    level.placedCells.filter { pc =>
      val a = pc.pos.x.toInt - (p.pos.x.toInt / 16)
      val b = pc.pos.y.toInt - (p.pos.y.toInt / 16)
      math.sqrt(a * a + b * b) < 10
    } foreach { pc =>
      val rgba = cellToPixel(pc)
      imageBuffer.setRGBA(pc.pos.x, pc.pos.y, rgba.r, rgba.g, rgba.b, rgba.a)
    }
    // Add player
    setLargePixel(player.pos.toVec2d / 16, RGBA(255, 0, 0, 255))
  }

}