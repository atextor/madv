package de.atextor.madv

package object engine {
  val Nowhere = Vec2f(0, 0)
  val OnePixelSize = Vec2d(1, 1)
  
  type Action = (Int => Unit)
  type TimedAction = (Int, Action)
  val NoAction: Action = (_ => ())
  val DoNothing = (() => ())
  implicit def noArg2intArg(f: (() => Unit)): Action = { i => f() }
  
  implicit def vec2d2vec2f(v: Vec2d) = Vec2f(v.x, v.y)
}