package de.atextor.madv.game

import org.newdawn.slick.SpriteSheet

import de.atextor.madv.engine.Constants
import de.atextor.madv.engine.EntitySkin
import de.atextor.madv.engine.Humanoid
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walk
import de.atextor.madv.engine.Walkable

class Player(level: Level, startPosition: Vec2d, entitySkin: EntitySkin) extends Humanoid(
    level = level,
    skin = entitySkin,
    spriteAction = Walk,
    startPosition = startPosition,
    speed = (if (Constants.debug) 5 else 1)
) {
  val staticRenderPos = Vec2d(168, 80)
  
  override def draw(x: Float, y: Float) = {
    shadow.draw(x, y + 5)
    skin.draw(lookingDirection, spriteAction, Vec2d(x.toInt, y.toInt)) 
  }
  
  override def touchTopLeft = pos + Vec2d(27, 25)
  override def touchBottomRight = pos + Vec2d(15, 18)
  
  override def move {
    pos += movingDirection * speed
    if (Constants.debug) return
    if (!(level.cellAt(pos).properties contains Walkable)) goBack
  }
}