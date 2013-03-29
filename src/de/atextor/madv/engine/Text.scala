package de.atextor.madv.engine

import org.newdawn.slick.Renderable
import org.newdawn.slick.UnicodeFont
import org.newdawn.slick.font.effects.ColorEffect
import java.awt.Color
import java.awt.Font

object Text {
  val unicodeFont = new UnicodeFont("res/fonts/cure.de.ttf", 11, false, false)
  unicodeFont.getEffects.asInstanceOf[java.util.List[AnyRef]].add(new ColorEffect(Color.white))
  unicodeFont.addAsciiGlyphs
  unicodeFont.addGlyphs("äöüÄÖÜ")
  unicodeFont.loadGlyphs
  def getTextHeight(text: String) = 11 * (1 + text.count(_ == '\n'))
  def getTextWidth(text: String) = text.split("\n").map(unicodeFont.getWidth(_)).max
}

class Text(var text: String, appear: Boolean = false) extends Renderable with Tickable {
  var appeared = if (appear) 0 else text.length
  def draw(x: Float, y: Float) = Text.unicodeFont.drawString(x, y, text.substring(0, appeared))
  def showAll = appeared = text.length
  def getWidth = Text.getTextWidth(text)
  def getHeight = Text.getTextHeight(text)
  def allShown = (!appear) || appeared == text.length
  
  private var lastTick = 0
  def tick(scene: Scene, delta: Int) {
    if (appeared < text.length && delta - lastTick > 50) {
      appeared += 1
      lastTick = delta
    }
  }
  override def toString = "Text(" + text + ")"
}