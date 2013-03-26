package de.atextor.madv.engine

import scala.concurrent.duration.DurationInt

sealed trait GameEffect

// Increase player stats
case class PlayerHealth(amount: Int) extends GameEffect
case class PlayerMaxHealth(amount: Int) extends GameEffect
case class PlayerSpeed(amount: Int) extends GameEffect
case class PlayerCastSpeed(amount: Int) extends GameEffect
case class PlayerArmor(amount: Int) extends GameEffect

// Global game effects
case object SlowMonsters extends GameEffect
case object RandomTeleport extends GameEffect
case object MagicMapping extends GameEffect
case object RearmChests extends GameEffect
case object MuffinDizzy extends GameEffect

object GameEffects {
  private def say(s: String) {
    
  }
  def apply(scene: Scene, ge: GameEffect, level: Level, automap: AutoMap, player: Player) = ge match {
    case PlayerHealth(a) => player.hurt(-a)
    case PlayerMaxHealth(a) => // TODO
    case PlayerSpeed(a) => player.speed += a; scene.in(30 seconds, (_ => player.speed -= a))
    case PlayerCastSpeed(a) => player.cooldownBoost += a; scene.in(60 seconds, (_ => player.cooldownBoost -=a)) 
    case PlayerArmor(a) => player.armor += a; scene.in(2 minutes, (_ => player.armor -= a))
    case SlowMonsters => // TODO
    case RandomTeleport => player.pos = level.find(_.cell.properties contains Walkable).get.pos * 16
    case MagicMapping => automap.uncoverMap(player)
    case RearmChests => scene.entities.filter(_.properties contains IsGoodRearmable).foreach(_.armed = true)
    case MuffinDizzy => // TODO
  }
  
}