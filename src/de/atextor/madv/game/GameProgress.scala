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
      
  def randomMonster(level: Level, player: Player, startPos: Vec2d): Entity = level.setting.difficulty match {
    case Easy =>
      val rand = Random.nextInt(100)
      if (rand < 80) {
        new FemaleOrc(level, player, new Chaser(player), startPos)
      } else {
        new FemaleArmoredOrc(level, player, new Chaser(player), startPos)
      }
    case Medium =>
      val rand = Random.nextInt(100)
      if (rand < 25) {
        new FemaleOrc(level, player, new Chaser(player), startPos)
      } else if (rand < 60) {
        new FemaleArmoredOrc(level, player, new Chaser(player), startPos)
      } else {
        new FemaleHeavyArmoredOrc(level, player, new Chaser(player), startPos)
      }
    case Hard =>
      val rand = Random.nextInt(100)
      if (rand < 65) {
        new Skeleton(level, player, new Chaser(player), startPos)
      } else {
        new ArmoredSkeleton(level, player, new Chaser(player), startPos)
      }
    case Boss => 
      new HeavyArmoredSkeleton(level, player, new Chaser(player), startPos)
  }
      
  def randomItem(ls: LevelSetting): GameItem = ls.difficulty match {
    case Easy => Random shuffle BasicItems head
    case Medium => Random shuffle MediumItems head
    case Hard => Random shuffle PremiumItems head
    case Boss => LargeHealthPotion()
  }
}
