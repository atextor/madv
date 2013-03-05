package de.atextor.madv.engine

import org.newdawn.slick.Renderable
import org.newdawn.slick.Animation
import org.newdawn.slick.SpriteSheet

trait Tickable {
  var enabled = true
  final def update(delta: Int) = if (enabled) tick(delta)
  def tick(delta: Int)
}

abstract class Entity(var size: Vec2d, val visual: Option[Animation] = None, override var pos: Vec2f) extends Movable with Tickable with Renderable {
  var lookingDirection: Direction = Down
  var movingDirection: Vec2f = Nowhere
  var alive = true
  def draw(x: Float, y: Float) = visual.foreach(_.draw(x, y))
  def xDistanceTo(other: Entity) = Math.abs(pos.x - other.pos.x)
  def yDistanceTo(other: Entity) = Math.abs(pos.y - other.pos.y)
  def distanceTo(other: Entity) = {
    val a = xDistanceTo(other)
    val b = yDistanceTo(other)
    Math.sqrt(a * a + b * b)
  }
  def relativeDraw(base: Vec[Float], staticOffset: Vec[Int]) {
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
    startPosition: Vec2f,
    val speed: Float) extends Entity(size = skin.size, pos = startPosition) {
  
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
    if (movingDirection != Nowhere) {
      pos += movingDirection * speed
      if (!(level.cellAt(pos.toVec2d).properties contains Walkable)) goBack
    }
  }
}
