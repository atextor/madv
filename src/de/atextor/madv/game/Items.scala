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
    "Feuert gefährliche Shuriken.", effect = Some(PlayerSpell("Shuriken", { player: Player =>
      val shooter = (pos: Vec2f) => new Projectile(spawner = player, visual = Entities.shuriken, speed = 2.5f, damage = 20, directional = true)
      new Shooter(shooter, 500 millis, Audio.shoot _)
    })))
