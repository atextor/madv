package de.atextor.madv.engine

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
//    if (!(level.cellAt(pos.toVec2d).properties contains Walkable)) goBack
  }
}