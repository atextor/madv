package de.atextor.madv.engine

import scala.concurrent.duration.DurationInt
import de.atextor.madv.engine.Util.pipelineSyntax
import de.atextor.madv.game.CenteredTextBox
import de.atextor.madv.game.FemaleOrc
import de.atextor.madv.game.Entities

sealed trait GameEffect

// Increase player stats
case class PlayerSpell(name: String, spell: Player => Spell) extends GameEffect
case class PlayerHealth(amount: Int) extends GameEffect
case object PlayerMaxHealth extends GameEffect
case class PlayerMaxHealthIncrease(amount: Int) extends GameEffect
case class PlayerSpeed(amount: Float) extends GameEffect
case class PlayerCastSpeed(amount: Int) extends GameEffect
case class PlayerArmor(amount: Int) extends GameEffect

// Other player centric effects
case object RandomTeleport extends GameEffect
case object ExitTeleport extends GameEffect
case object TalkToMuffin extends GameEffect

// Global game effects
case object SlowMonsters extends GameEffect
case object MagicMapping extends GameEffect
case object RearmChests extends GameEffect
case object MuffinDizzy extends GameEffect
case object SpawnMonster extends GameEffect

object GameEffects {
  private def say(s: String, scene: Scene) {
    val text = new CenteredTextBox(width = 250, text = s)
    scene.addOverlay(text)
    scene.in(5 seconds, (_ => text.alive = false))
  }
  
  def apply(scene: Scene, ge: GameEffect, level: Level, automap: AutoMap, player: Player) = ge match {
    case PlayerSpell(name, spell) =>
      player.spell = Some(spell(player))
      scene.setMenu(None)
      say(s"${name} aktiviert.", scene)
    case PlayerHealth(a) =>
      player.hurt(-a)
      say(s"${a} Lebenspunkte geheilt.", scene)
    case PlayerMaxHealth =>
      player.hp = player.maxHp
      say("Lebenspunkte voll geheilt.", scene)
    case PlayerMaxHealthIncrease(a) =>
      player.maxHp += a
      player.hurt(-a)
      say("Maximale Lebenspunkte erhöht.", scene)
    case PlayerSpeed(a) =>
      player.speed += a
      scene.in(30 seconds, (_ => player.speed -= a))
      say("Du fühlst dich beschleunigt.", scene)
    case PlayerCastSpeed(a) =>
      player.cooldownBoost += a
      scene.in(60 seconds, (_ => player.cooldownBoost -=a)) 
      say("Du schleuderst Magie schneller.", scene)
    case PlayerArmor(a) =>
      player.armor += a
      scene.in(2 minutes, (_ => player.armor -= a))
      say("Du fühlst dich widerstandsfähig.", scene)
    case SlowMonsters =>
      // TODO
    case RandomTeleport =>
      val oldpos = player.pos.toVec2d
      player.pos = level.find(_.cell.properties contains Walkable).get.pos * 16
      automap.update(oldpos)
      Audio.teleport.play
      say("Du findest dich woanders wieder.", scene)
    case ExitTeleport =>
      level.find(_.cell.properties contains Exit).foreach { e =>
        val oldpos = player.pos.toVec2d
        player.pos = (e.pos + Down * 2) * 16
        automap.update(oldpos)
        Audio.teleport.play
        say("Du findest dich woanders wieder.", scene)
      }
    case MagicMapping =>
      automap.uncoverMap(player)
      say("Die Karte ist kein Geheimnis mehr.", scene)
    case RearmChests =>
      scene.entities.filter(_.properties contains IsGoodRearmable).foreach(_.rearm)
      say("Was offen war, ist wieder geschlossen.", scene)
    case MuffinDizzy =>
      // TODO
    case SpawnMonster => 
      level.find(_.cell.properties contains Walkable).get.pos * 16 |>
        (new FemaleOrc(level, player, new Chaser(player), _)) |>
        (scene.addEntity(_))
      say("Ein weiteres Monster wurde erschaffen.", scene)
    case TalkToMuffin =>
      scene.setMenu(None)
      scene.addStoryText(new StoryText("Ich könnte dir etwas verkaufen", Some(Entities.muffinPortrait),
          onClose = { () => scene.setMenu(Some(Shop)) }))
  }
  
}