package de.atextor.madv.game

import scala.util.Random
import de.atextor.madv.engine.GameItem
import de.atextor.madv.engine.LevelSetting
import de.atextor.madv.engine.Easy
import de.atextor.madv.engine.Medium
import de.atextor.madv.engine.Hard
import de.atextor.madv.engine.Boss
import de.atextor.madv.engine.Brain
import de.atextor.madv.engine.Vec2f
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.Player
import de.atextor.madv.engine.Entity
import de.atextor.madv.engine.Chaser
import de.atextor.madv.engine.Vec2d

object GameProgress {
  val BasicItems = List(SmallHealthPotion(), MediumHealthPotion(), SpeedPotion(), DefenseScroll(), RandomTeleportScroll())
  val MediumItems = List(MediumHealthPotion(), LargeHealthPotion(), SpeedPotion(), DefenseScroll(), AttackScroll(),
      MaxHealthPotion(), RearmChestsScroll(), MagicMapScroll(), RandomTeleportScroll(), IncreaseMaxHealthPotion())
  val PremiumItems = List(LargeHealthPotion(), MaxHealthPotion(), IncreaseMaxHealthPotion(), MagicMapScroll(),
      RearmChestsScroll(), SpeedPotion(), AttackScroll(), DefenseScroll(), ExitTeleportScroll())
      
  def randomMonster(level: Level, player: Player, startPos: Vec2d): Option[Entity] = level.setting.difficulty match {
    case Easy => Some(new FemaleOrc(level, player, new Chaser(player), startPos))
    case Medium => None
    case Hard => None
    case Boss => None
  }
      
  def randomItem(ls: LevelSetting): GameItem = ls.difficulty match {
    case Easy => Random shuffle BasicItems head
    case Medium => Random shuffle MediumItems head
    case Hard => Random shuffle PremiumItems head
    case Boss => LargeHealthPotion()
  }
}
