package de.atextor.madv.engine

import org.newdawn.slick.Renderable
import org.newdawn.slick.UnicodeFont
import org.newdawn.slick.font.effects.ColorEffect
import java.awt.Color

object Text {
  val unicodeFont = new UnicodeFont("res/fonts/cure.se.ttf", 11, false, false)
  unicodeFont.getEffects.asInstanceOf[java.util.List[AnyRef]].add(new ColorEffect(Color.white))
  def getTextHeight(text: String) = unicodeFont.getHeight(text) * (1 + text.count(_ == '\n'))
}

class Text(text: String, appear: Boolean = false) extends Renderable with Tickable {
  var appeared = if (appear) 0 else text.length
  def draw(x: Float, y: Float) = Text.unicodeFont.drawString(x, y, text.substring(0, appeared))
  def showAll = appeared = text.length
  def getWidth = Text.unicodeFont.getWidth(text)
  def getHeight = Text.getTextHeight(text)
  
  private var lastTick = 0
  def tick(delta: Int) {
    if (appeared < text.length && delta - lastTick > 50) {
      appeared += 1
      lastTick = delta
    }
  }
}