package de.atextor.madv.engine

abstract class Vec(val x: Int, val y: Int)

sealed abstract class Direction(val id: Int, x: Int, y: Int) extends Vec(x, y)
case object Up      extends Direction(id = 0, x = 0,  y = -1)
case object Left    extends Direction(id = 1, x = -1, y = 0)
case object Down    extends Direction(id = 2, x = 0,  y = 1)
case object Right   extends Direction(id = 3, x = 1,  y = 0)

case class Vec2d(override val x: Int, override val y: Int) extends Vec(x, y) {
  def +(v: Vec): Vec2d = Vec2d(x + v.x, y + v.y)
  def -(v: Vec): Vec2d = Vec2d(x - v.x, y - v.y)
  def *(f: Int): Vec2d = Vec2d(x * f, y * f)
  def /(f: Int): Vec2d = Vec2d(x / f, y / f)
  def apply(d: Direction) = Vec2d(d.x, d.y)
}
