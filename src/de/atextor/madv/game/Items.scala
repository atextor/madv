package de.atextor.madv.game

import de.atextor.madv.engine.GameItem
import de.atextor.madv.engine.MagicMapping
import de.atextor.madv.engine.PlayerHealth
import de.atextor.madv.engine.RearmChests
import de.atextor.madv.engine.PlayerSpeed
import de.atextor.madv.engine.PlayerCastSpeed
import de.atextor.madv.engine.PlayerArmor
import de.atextor.madv.engine.RandomTeleport
import de.atextor.madv.engine.SpawnMonster
import de.atextor.madv.engine.ExitTeleport

case class SmallHealthPotion() extends GameItem("Kleiner Heiltrank",
    "Regeneriert 5 Lebenspunkte", Some(PlayerHealth(5)))

case class MediumHealthPotion() extends GameItem("Mittlerer Heiltrank",
    "Regeneriert 20 Lebenspunkte", Some(PlayerHealth(10)))

case class LargeHealthPotion() extends GameItem("Starker Heiltrank",
    "Regeneriert 50 Lebenspunkte", Some(PlayerHealth(50)))

case class MagicMapScroll() extends GameItem("Schriftrolle der Kartographie",
    "Warum selbst erkunden, was Magie\nfür einen erledigen kann?", Some(MagicMapping))

case class RearmChestsScroll() extends GameItem("Schriftrolle des Füllhorns",
    "Verspricht ungeahnte Reichtümer.", Some(RearmChests))

case class SpeedPotion() extends GameItem("Energy Drink",
    "Beschleunigt dich für\n30 Sekunden", Some(PlayerSpeed(0.5f)))

case class AttackScroll() extends GameItem("Schriftrolle der Offensive",
    "Erhöht die Angriffs-\ngeschwindigkeit um 10%", Some(PlayerCastSpeed(50)))

case class DefenseScroll() extends GameItem("Schriftrolle der Defensive",
    "Absorbiert erlittenen Schaden\n2 Minuten lang.", Some(PlayerArmor(1)))

case class RandomTeleportScroll() extends GameItem("Schriftrolle des Chaos",
    "Teleportiert an eine\nzufällige Stelle", Some(RandomTeleport))

case class ExitTeleportScroll() extends GameItem("Schriftrolle der Faulheit",
    "Teleportiert direkt\nzum Ausgang", Some(ExitTeleport))

case class SpawnMonsterScroll() extends GameItem("Schriftrolle des Monsters",
    "Lässt ein Monster entstehen.", Some(SpawnMonster))