package de.atextor.madv.game

import scala.concurrent.duration.DurationInt
import de.atextor.madv.engine.Audio
import de.atextor.madv.engine.ExitTeleport
import de.atextor.madv.engine.GameItem
import de.atextor.madv.engine.MagicMapping
import de.atextor.madv.engine.Player
import de.atextor.madv.engine.PlayerArmor
import de.atextor.madv.engine.PlayerCastSpeed
import de.atextor.madv.engine.PlayerHealth
import de.atextor.madv.engine.PlayerMaxHealth
import de.atextor.madv.engine.PlayerMaxHealthIncrease
import de.atextor.madv.engine.PlayerSpeed
import de.atextor.madv.engine.PlayerSpell
import de.atextor.madv.engine.Projectile
import de.atextor.madv.engine.RandomTeleport
import de.atextor.madv.engine.RearmChests
import de.atextor.madv.engine.Shooter
import de.atextor.madv.engine.SpawnMonster
import de.atextor.madv.engine.Vec2f
import de.atextor.madv.engine.Spell
import de.atextor.madv.engine.Up
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.Left
import de.atextor.madv.engine.Right
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walkable
import de.atextor.madv.engine.DoNothing
import com.sun.media.sound.AutoClosingClip

// Inventory items
case class SmallHealthPotion() extends GameItem("Kleiner Heiltrank",
    "Regeneriert 5 Lebenspunkte", effect = Some(PlayerHealth(5)))

case class MediumHealthPotion() extends GameItem("Mittlerer Heiltrank",
    "Regeneriert 20 Lebenspunkte", effect = Some(PlayerHealth(10)))

case class LargeHealthPotion() extends GameItem("Starker Heiltrank",
    "Regeneriert 50 Lebenspunkte", effect = Some(PlayerHealth(50)))

case class MaxHealthPotion() extends GameItem("Maximal-Heiltrank",
    "Regeneriert die vollen Lebenspunkte", effect = Some(PlayerMaxHealth))

case class IncreaseMaxHealthPotion() extends GameItem("Lebenspunkte-Trank",
    "Erhöht dauerthaft die\nmaximalen Lebenspunkte", effect = Some(PlayerMaxHealthIncrease(10)))

case class MagicMapScroll() extends GameItem("Schriftrolle der Kartographie",
    "Warum selbst erkunden, was Magie\nfür einen erledigen kann?", effect = Some(MagicMapping))

case class RearmChestsScroll() extends GameItem("Schriftrolle des Füllhorns",
    "Verspricht ungeahnte Reichtümer.", effect = Some(RearmChests))

case class SpeedPotion() extends GameItem("Energy Drink",
    "Beschleunigt dich für\n30 Sekunden", effect = Some(PlayerSpeed(0.5f)))

case class AttackScroll() extends GameItem("Schriftrolle der Offensive",
    "Erhöht die Angriffs-\ngeschwindigkeit um 10%", effect = Some(PlayerCastSpeed(50)))

case class DefenseScroll() extends GameItem("Schriftrolle der Defensive",
    "Absorbiert erlittenen Schaden\n2 Minuten lang.", effect = Some(PlayerArmor(1)))

case class RandomTeleportScroll() extends GameItem("Schriftrolle des Chaos",
    "Teleportiert an eine\nzufällige Stelle", effect = Some(RandomTeleport))

case class ExitTeleportScroll() extends GameItem("Schriftrolle der Faulheit",
    "Teleportiert direkt\nzum Ausgang", Some(ExitTeleport))

case class SpawnMonsterScroll() extends GameItem("Schriftrolle des Monsters",
    "Lässt ein Monster entstehen.", effect = Some(SpawnMonster))

// Spell items
case class ShurikenSpell() extends GameItem("Shuriken",
  "Feuert scharfkantige Ninjasterne.", effect = Some(PlayerSpell("Shuriken", { player: Player =>
    val shooter = (pos: Vec2f) => new Projectile(
      spawner = player,
      visual = Entities.shuriken,
      speed = 3.5f,
      damage = 10,
      directional = true)
    new Shooter(shooter, 500 millis, Audio.shoot _)
  })))

case class BallLightningSpell() extends GameItem("Kugelblitz",
  "Feuert einen langsamen, aber\ngefährlichen elektrischen Blitz", effect = Some(PlayerSpell("Kugelblitz", { player: Player =>
    val shooter = (pos: Vec2f) => new Projectile(
      spawner = player,
      visual = Entities.sparkle1,
      speed = 1.8f,
      damage = 25,
      directional = false)
    new Shooter(shooter, 1 seconds, Audio.shoot _)
  })))

case class SpiralSpell() extends GameItem("Wirbel",
  "Feuert einen Wirbel aus Energie.", effect = Some(PlayerSpell("Wirbel", { player: Player =>
    val shooter = (pos: Vec2f) => new Projectile(
      spawner = player,
      visual = Entities.spiral,
      speed = 1.0f,
      damage = 30,
      directional = false)
    new Shooter(shooter, 500 millis, Audio.shoot _)
  })))

case class JumpSpell() extends GameItem("Sprung",
  "Teleportiert auf die nächste\nbetretbare Stelle in Blickrichtung", effect = Some(PlayerSpell("Sprung", { player: Player =>
     new Spell(cooldown = 1 seconds, onFire = DoNothing) {
       def apply(pos: Vec2f) = {
         val dir = player.lookingDirection
         val cur = player.pos.toVec2d
         val coords = dir match {
           case Up => (1 to cur.y - 8 by 8).toList.reverse.map(Vec2d(cur.x, _))
           case Right => (cur.x + 8 to player.level.width * 16 - 1 by 8).toList.map(Vec2d(_, cur.y))
           case Down => (cur.y + 8 to player.level.height * 16 - 1 by 8).toList.map(Vec2d(cur.x, _))
           case Left => (1 to cur.x - 8 by 8).toList.reverse.map(Vec2d(_, cur.y))
         }
         val target = coords.find(player.level.cellAt(_).properties contains Walkable)
         target.foreach { t =>
           val oldpos = player.pos.toVec2d
           player.pos = t
           player.autoMap.foreach(_.update(oldpos))
           Audio.teleport.play
         }
         
         // This spell does not create new entities
         Nil
       }
     } 
  })))
