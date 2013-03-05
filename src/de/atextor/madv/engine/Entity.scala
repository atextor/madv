package de.atextor.madv.engine

import org.newdawn.slick.Renderable
import org.newdawn.slick.Animation
import org.newdawn.slick.SpriteSheet

trait Tickable {
  var enabled = true
  final def update(delta: Int) = if (enabled) tick(delta)
  def tick(delta: Int)
}

abstract class Entity(var size: Vec2d, val visual: Option[Animation] = None, override var pos: Vec2d) extends Movable with Tickable with Renderable {
  var lookingDirection: Direction = Down
  var movingDirection: Vec2d = Nowhere
  var alive = true
  def draw(x: Float, y: Float) = visual.foreach(_.draw(x, y))
  def distanceTo(other: Entity) = {
    val a = Math.abs(pos.x - other.pos.x)
    val b = Math.abs(pos.y - other.pos.y)
    Math.sqrt(a * a + b * b)
  }
  def relativeDraw(base: Vec, staticOffset: Vec) {
    if (enabled) {
      draw((pos.x - base.x) * 2 + staticOffset.x + 8, (pos.y - base.y) * 2 + staticOffset.y + 32)
    }
  }
}

abstract class Brain extends (Humanoid => Unit)

object Dumb extends Brain {
  def apply(h: Humanoid) { }
}

class Humanoid (
    level: Level,
    var skin: EntitySkin,
    behavior: Brain = Dumb,
    var spriteAction: SpriteAction = Walk,
    startPosition: Vec2d,
    val speed: Int) extends Entity(size = skin.size, pos = startPosition) {
  
  val shadow = new SpriteSheet("res/sprites/humanoid_shadow.png", 64, 64).getSprite(0, 0)
  def tick(delta: Int) = behavior(this)
  
  override def draw(x: Float, y: Float) = {
    shadow.draw(x - 8, y - 26)
    skin.draw(lookingDirection, spriteAction, Vec2d(x.toInt - 8, y.toInt - 31)) 
  }
  
  def stop = {
    movingDirection = Nowhere
    skin.stopAnimation(lookingDirection, spriteAction)
  }
  
  def go(d: Direction) {
    movingDirection = movingDirection(d)
    lookingDirection = d
    skin.startAnimation(lookingDirection, spriteAction)
  }
  
  def goBack = pos += movingDirection.invert * speed 
  
  override def move = {
    pos += movingDirection * speed
    if (!(level.cellAt(pos).properties contains Walkable)) goBack
  }
    
//  override def move = {
//    pos += movingDirection * speed
//    true
//  }
}
