package de.atextor.madv

package object engine {
  val Nowhere = Vec2f(0, 0)
  
  type Action = (Int => Unit)
  type TimedAction = (Int, Action)
  val DoNothing: Action = (_ => ())
  implicit def noArg2intArg(f: (() => Unit)): Action = { i => f() }
  
  implicit def vec2d2vec2f(v: Vec2d) = Vec2f(v.x, v.y)
}