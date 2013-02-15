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
    skin = entitySkin,
    spriteAction = Walk,
    startPosition = startPosition,
    speed = (if (Constants.debug) 5 else 1)
) {
  val shadow = new SpriteSheet("res/sprites/humanoid_shadow.png", 64, 64).getSprite(0, 0)
  val staticRenderPos = Vec2d(170, 80)
  
  override def draw(x: Float, y: Float) = {
    shadow.draw(staticRenderPos.x, staticRenderPos.y + 5)
    skin.draw(lookingDirection, spriteAction, staticRenderPos)
  }
  
  override def touchTopLeft = pos + Vec2d(23, 17)
  override def touchBottomRight = pos + Vec2d(15, 18)
  
  override def move = {
    super.move
    if (Constants.debug) {
      true
    } else {
      if (level.cellAt(pos).properties contains Walkable) {
        true 
      } else {
        pos += movingDirection.invert * speed 
        false
      }
    }
  }
}