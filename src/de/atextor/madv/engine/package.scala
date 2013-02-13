package de.atextor.madv
package object engine {
  val Nowhere = Vec2d(0, 0)
  
  type Action = (Int => Unit)
  type TimedAction = (Int, Action)
  val DoNothing: Action = (_ => ())
}