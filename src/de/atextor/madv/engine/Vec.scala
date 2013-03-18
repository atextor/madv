package de.atextor.madv.engine

import scala.math.Numeric

abstract class Vec[T: Numeric](val x: T, val y: T)

sealed abstract class Direction(val id: Int, x: Int, y: Int) extends Vec[Int](x, y) {
  def +(v: Vec[Int]): Vec2d = Vec2d(x + v.x, y + v.y)
  def *(f: Int): Vec2d = Vec2d(x * f, y * f)
}
case object Up    extends Direction(id = 0, x = 0,  y = -1)
case object Left  extends Direction(id = 1, x = -1, y = 0)
case object Down  extends Direction(id = 2, x = 0,  y = 1)
case object Right extends Direction(id = 3, x = 1,  y = 0)

case class Vec2d(override val x: Int, override val y: Int) extends Vec[Int](x, y) {
  def +(v: Vec[Int]): Vec2d = Vec2d(x + v.x, y + v.y)
  def -(v: Vec[Int]): Vec2d = Vec2d(x - v.x, y - v.y)
  def *(f: Int): Vec2d = Vec2d(x * f, y * f)
  def /(f: Int): Vec2d = Vec2d(x / f, y / f)
  def apply(d: Direction) = Vec2d(d.x, d.y)
  def invert = Vec2d(-x, -y)
  def toVec2f = Vec2f(x.toFloat, y.toFloat)
}

case class Vec2f(override val x: Float, override val y: Float) extends Vec[Float](x, y) {
  def +(v: Vec[Float]): Vec2f = Vec2f(x + v.x, y + v.y)
  def +(v: Vec[Int]): Vec2d = Vec2d(x.toInt + v.x, y.toInt + v.y)
  def -(v: Vec[Float]): Vec2f = Vec2f(x - v.x, y - v.y)
  def *(f: Float): Vec2f = Vec2f(x * f, y * f)
  def /(f: Float): Vec2f = Vec2f(x / f, y / f)
  def apply(d: Direction) = Vec2f(d.x, d.y)
  def invert = Vec2f(-x, -y)
  def toVec2d = Vec2d(x.toInt, y.toInt)
}
