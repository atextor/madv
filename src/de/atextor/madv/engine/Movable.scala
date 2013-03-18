package de.atextor.madv.engine

import scala.collection.mutable.ListBuffer

trait Movable {
  var pos: Vec2f
  var size: Vec2d
  var movingDirection: Vec2f
  
  def middle: Vec2d = Vec2d(pos.x.toInt + size.x / 2, pos.y.toInt + size.y / 2)
  
  def touchTopLeft = pos
  
  def touchBottomRight = pos + size.toVec2f
  
  def touches(other: Movable): Boolean = {
    val thisBR = touchBottomRight
    val otherBR = other.touchBottomRight
    !((other.touchTopLeft.x > thisBR.x) ||
      (other.touchTopLeft.y > thisBR.y) ||
      (touchTopLeft.x > otherBR.x) ||
      (touchTopLeft.y > otherBR.y))
  }
      
  def move = pos += movingDirection
}
  
