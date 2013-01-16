package de.atextor.madv.engine

trait Tickable {
  def tick(delta: Int)
}

trait Drawable {
  def draw
}

abstract class Entity(skin: EntitySkin) extends Tickable with Drawable

case class Humanoid(
    skin: EntitySkin,
    behavior: (Int => Unit),
    direction: Direction,
    action: Action,
    position: Vec) extends Entity(skin) {
  def tick(delta: Int) = behavior(delta)
  def draw = skin.draw(direction, action, position)
}