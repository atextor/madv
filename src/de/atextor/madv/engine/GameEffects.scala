package de.atextor.madv.engine

sealed trait GameEffect
case object MagicMapping extends GameEffect
case object PlayerHealth extends GameEffect

object GameEffects {
  def apply(ge: GameEffect, level: Level, automap: AutoMap, target: Option[Entity], player: Player) = ge match {
    case MagicMapping => automap.uncoverMap(player)
    case PlayerHealth =>
  }
  
}