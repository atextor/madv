package de.atextor.madv.game

import scala.concurrent.duration.DurationInt

import de.atextor.madv.engine.Audio
import de.atextor.madv.engine.DoNothing
import de.atextor.madv.engine.GameItem
import de.atextor.madv.engine.ItemGameItem
import de.atextor.madv.engine.Player
import de.atextor.madv.engine.Projectile
import de.atextor.madv.engine.Shooter
import de.atextor.madv.engine.Spell
import de.atextor.madv.engine.SpellGameItem
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Vec2f
import de.atextor.madv.engine.Up
import de.atextor.madv.engine.Right
import de.atextor.madv.engine.Left
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.Walkable

// Inventory items
case class Muffin extends GameItem(ItemGameItem, "Magischer Muffin",
    "Ein seltsamer sprechender\nMuffin.", price = 0, effect = Some(TalkToMuffin))

case class SmallHealthPotion() extends GameItem(ItemGameItem, "Kleiner Heiltrank",
    "Regeneriert 5 Lebenspunkte", price = 10, effect = Some(PlayerHealth(5)))

case class MediumHealthPotion() extends GameItem(ItemGameItem, "Mittlerer Heiltrank",
    "Regeneriert 20 Lebenspunkte", price = 20, effect = Some(PlayerHealth(10)))

case class LargeHealthPotion() extends GameItem(ItemGameItem, "Starker Heiltrank",
    "Regeneriert 50 Lebenspunkte", price = 80, effect = Some(PlayerHealth(50)))

case class MaxHealthPotion() extends GameItem(ItemGameItem, "Maximal-Heiltrank",
    "Regeneriert die vollen Lebenspunkte", price = 150, effect = Some(PlayerMaxHealth))

case class IncreaseMaxHealthPotion() extends GameItem(ItemGameItem, "Lebenspunkte-Trank",
    "Erhöht dauerthaft die\nmaximalen Lebenspunkte", price = 250, effect = Some(PlayerMaxHealthIncrease(10)))

case class MagicMapScroll() extends GameItem(ItemGameItem, "Schriftrolle der Kartographie",
    "Warum selbst erkunden, was Magie\nfür einen erledigen kann?", price = 400, effect = Some(MagicMapping))

case class RearmChestsScroll() extends GameItem(ItemGameItem, "Schriftrolle des Füllhorns",
    "Verspricht ungeahnte Reichtümer.", price = 800, effect = Some(RearmChests))

case class SpeedPotion() extends GameItem(ItemGameItem, "Energy Drink",
    "Beschleunigt dich für\n30 Sekunden", price = 70, effect = Some(PlayerSpeed(0.5f)))

case class AttackScroll() extends GameItem(ItemGameItem, "Schriftrolle der Offensive",
    "Erhöht die Angriffs-\ngeschwindigkeit um 10%", price = 150, effect = Some(PlayerCastSpeed(50)))

case class DefenseScroll() extends GameItem(ItemGameItem, "Schriftrolle der Defensive",
    "Absorbiert erlittenen Schaden\n2 Minuten lang.", price = 150, effect = Some(PlayerArmor(1)))

case class RandomTeleportScroll() extends GameItem(ItemGameItem, "Schriftrolle des Chaos",
    "Teleportiert an eine\nzufällige Stelle", price = 50, effect = Some(RandomTeleport))

case class ExitTeleportScroll() extends GameItem(ItemGameItem, "Schriftrolle der Faulheit",
    "Teleportiert direkt\nzum Ausgang", price = 200, Some(ExitTeleport))

case class SpawnMonsterScroll() extends GameItem(ItemGameItem, "Schriftrolle des Monsters",
    "Lässt ein Monster entstehen.", price = 30, effect = Some(SpawnMonster))

// Spell items
case class ShurikenSpell() extends GameItem(SpellGameItem, "Shuriken",
  "Feuert scharfkantige Ninjasterne.", price = 1000, effect = Some(PlayerSpell("Shuriken", { player: Player =>
    val shooter = (pos: Vec2f) => new Projectile(
      spawner = player,
      visual = Entities.shuriken,
      speed = 3.5f,
      damage = 10,
      directional = true)
    new Shooter(shooter, 500 millis, Audio.shoot _)
  })))

case class BallLightningSpell() extends GameItem(SpellGameItem, "Kugelblitz",
  "Feuert einen langsamen, aber\ngefährlichen elektrischen Blitz.", price = 1500, effect = Some(PlayerSpell("Kugelblitz", { player: Player =>
    val shooter = (pos: Vec2f) => new Projectile(
      spawner = player,
      visual = Entities.sparkle1,
      speed = 1.8f,
      damage = 25,
      directional = false)
    new Shooter(shooter, 1 seconds, Audio.shoot _)
  })))

case class SpiralSpell() extends GameItem(SpellGameItem, "Wirbel",
  "Feuert einen Wirbel aus Energie,\nder bei Einschlag explodiert.", price = 1600, effect = Some(PlayerSpell("Wirbel", { player: Player =>
    val shooter = (pos: Vec2f) => new Projectile(
      spawner = player,
      visual = Entities.spiral,
      speed = 1.0f,
      damage = 30,
      directional = false,
      onHit = { p: Vec2f => 
        Audio.explosion.play
        new Explosion(p) :: Nil
      })
    new Shooter(shooter, 500 millis, Audio.shoot _)
  })))

case class JumpSpell() extends GameItem(SpellGameItem, "Sprung",
  "Teleportiert auf die nächste\nbetretbare Stelle in Blickrichtung", price = 1000, effect = Some(PlayerSpell("Sprung", { player: Player =>
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
