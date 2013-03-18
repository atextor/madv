package de.atextor.madv.engine

import scala.concurrent.duration.DurationInt

abstract class Brain extends ((Humanoid, Int) => Unit)

object Dumb extends Brain {
  def apply(h: Humanoid, ticks: Int) { }
}

object Dying extends Brain {
  def apply(me: Humanoid, delta: Int) {
    me.at(delta + 700 millis, {_ => me.movingDirection = Nowhere})
    me.at(delta + 5000 millis, {_ => me.alive = false})
    me.behavior = Dumb
  }
}

class Attack(player: Player, damage: Int) extends Brain {
  var armed = true
  def apply(me: Humanoid, delta: Int) {
    val dist = player distanceTo me
    if (dist > 20) {
      me.movingDirection = Nowhere
      me.at(delta + 1000 millis, {_ => me.chase})
      return
    }
    
    if (armed && player.alive) {
      armed = false
      player.hurt(damage)
      me.at(delta + 100 millis, {_ => armed = true})
    }
  }
}
    
class Chaser(player: Player) extends Brain {
  val eps = 2.0
  def apply(me: Humanoid, delta: Int) {
    val dist = player distanceTo me
    if (dist < 20) {
      me.attack
      return
    }
    
    if (dist < 500) {
      val xdist = Math.abs(me.pos.x.toInt / 16 - player.pos.x.toInt / 16)
      val ydist = Math.abs(me.pos.y.toInt / 16 - player.pos.y.toInt / 16)
      if (xdist > ydist) {
        if (me.pos.x > player.pos.x + eps) { me go Left; if (!me.canMove) tryUpDown(me); return }
        if (me.pos.x < player.pos.x - eps) { me go Right; if (!me.canMove) tryUpDown(me); return }
      } else {
        if (me.pos.y > player.pos.y + eps) { me go Up; if (!me.canMove) tryLeftRight(me); return }
        if (me.pos.y < player.pos.y - eps) { me go Down; if (!me.canMove) tryLeftRight(me); return }
      }
    }
  }
  
  private def tryUpDown(me: Humanoid) {
    if (me.pos.y > player.pos.y + eps) { me go Up; if(!me.canMove) me.stop; return }
    if (me.pos.y < player.pos.y - eps) { me go Down; if(!me.canMove) me.stop; return }
    me.stop
  }
  
  private def tryLeftRight(me: Humanoid) {
    if (me.pos.x > player.pos.x + eps) { me go Left; if(!me.canMove) me.stop; return }
    if (me.pos.x < player.pos.x - eps) { me go Right; if(!me.canMove) me.stop; return }
    me.stop
  }
}

