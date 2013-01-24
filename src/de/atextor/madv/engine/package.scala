package de.atextor.madv
/*
 * These directions are defined for the sake of adding vectors and making code more readable,
 * but are not Directions as defined in Vec2d.scala, because sprites can not face diagonally.
 */
package object engine {
  val UpLeft = Vec2d(-1, -1)
  val UpRight = Vec2d(1, -1)
  val DownLeft = Vec2d(-1, 1)
  val DownRight = Vec2d(1, 1)
}