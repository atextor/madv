package de.atextor.madv.engine

import org.newdawn.slick.Renderable
import org.newdawn.slick.Animation

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

abstract class Overlay(var pos: Vec2d) extends Tickable {
  def draw
  var alive = true
  var active = true
}

class Humanoid (
    var skin: EntitySkin,
    behavior: Action = DoNothing,
    var spriteAction: SpriteAction = Walk,
    startPosition: Vec2d,
    val speed: Int) extends Entity(size = skin.size, pos = startPosition) {
  def tick(delta: Int) = behavior(delta)
  
  override def draw(x: Float, y: Float) = skin.draw(lookingDirection, spriteAction, Vec2d(x.toInt, y.toInt))
  
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
