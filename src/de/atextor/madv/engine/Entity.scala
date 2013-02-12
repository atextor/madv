package de.atextor.madv.engine

trait Tickable {
  def tick(delta: Int)
}

trait Drawable {
  def draw
}

abstract class Entity(skin: EntitySkin, override var pos: Vec2d) extends Movable with Tickable with Drawable {
  var size = skin.size
  var lookingDirection: Direction = Down
  var movingDirection: Vec2d = Nowhere
  var alive = true
}

class Humanoid (
    var skin: EntitySkin,
    behavior: Action = DoNothing,
    var spriteAction: SpriteAction = Walk,
    startPosition: Vec2d,
    val speed: Int) extends Entity(skin = skin, pos = startPosition) {
  def tick(delta: Int) = behavior(delta)
  def draw = skin.draw(lookingDirection, spriteAction, pos)
  def stop = {
    movingDirection = Nowhere
    skin.stopAnimation(lookingDirection, spriteAction)
  }
  def go(d: Direction) {
    movingDirection = movingDirection(d)
    lookingDirection = d
    skin.startAnimation(lookingDirection, spriteAction)
  }
  override def move = {
    pos += movingDirection * speed
    true
  }
}
