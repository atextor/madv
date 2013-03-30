package de.atextor.madv.game

import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import scala.io.Source
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Input
import org.newdawn.slick.state.StateBasedGame
import de.atextor.madv.engine.AutoMap
import de.atextor.madv.engine.BlueCave
import de.atextor.madv.engine.Constants
import de.atextor.madv.engine.NoAction
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.Left
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.Right
import de.atextor.madv.engine.Scene
import de.atextor.madv.engine.Text
import de.atextor.madv.engine.Up
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walkable
import de.atextor.madv.engine.Action
import de.atextor.madv.engine.Inventory
import de.atextor.madv.engine.Dumb
import de.atextor.madv.engine.Humanoid
import de.atextor.madv.engine.Inventory
import de.atextor.madv.engine.StoryText
import de.atextor.madv.engine.Player
import de.atextor.madv.engine.Chaser
import de.atextor.madv.engine.HealthDisplay
import de.atextor.madv.engine.Vec2f
import de.atextor.madv.engine.Projectile
import de.atextor.madv.engine.Shooter
import de.atextor.madv.engine.Audio
import de.atextor.madv.engine.SpellSelection
import de.atextor.madv.engine.Shop
import de.atextor.madv.engine.GameItem
import de.atextor.madv.engine.Entity
import de.atextor.madv.engine.CoherentBlue
import de.atextor.madv.engine.IslandBlack
import de.atextor.madv.engine.IslandLava
import de.atextor.madv.engine.CoherentLava
import de.atextor.madv.engine.IslandGreen
import de.atextor.madv.engine.CoherentGreen
import de.atextor.madv.engine.TextBox

class LevelTest(toggleFullscreen: () => Unit) extends Scene(toggleFullscreen) {
  override val getID = 1
  val playerSkin = Entities.playerSkin
  
//  val levelSettings = List(CoherentGreen, CoherentBlue, CoherentLava, IslandGreen, IslandLava, IslandBlack)
  val levelSettings = List(IslandBlack)
  val currentLevelSetting = levelSettings.iterator
  
  def win {
    val outro: Action = { ticks =>
      val lines = Source.fromFile("res/outro.txt").getLines.map(_.replace("|", "\n"))
      lines.zipWithIndex.foreach { case (l, i) =>
        val text = new TextBox(350, l, Vec2d(20, 180))
        in((i * 3) seconds, { t => addOverlay(text) })
        in(((i + 1) * 3) seconds, { t => text.alive = false })
      }
//      in((lines.size + 2) * 3 seconds, t => exitScene)
    }
    
    val text = new CenteredTextBox(width = 250, text = "Die Kiste enthielt:\nEinen Bandring")
    addOverlay(text)
    in(5 seconds, (_ => text.alive = false))
    addStoryText(new StoryText("Geschafft", Some(Entities.muffinPortrait)))
    addStoryText(new StoryText("Foobar ", Some(Entities.muffinPortrait), onClose = () => {
      in(0 seconds, { t => outro(t) })
    }))
  }
  
  def nextLevelSetting = {
    Audio.slash.stop
    currentLevelSetting.next
  }
  
  def levelTransformations(l: Level): Level = {
    Entities.placeChestsInLevel(l, this)
  }
  
  def levelDecorations(l: Level): Seq[Entity] = {
    val ents = Entities.placeEntitiesInLevel(player, l)
    numMonsters = ents._1
    ents._2
  }
  
  def init(gc: GameContainer, game: StateBasedGame) {
    startNewLevel
    at(0 millis, t => player.stop)
    
    Inventory.addItem(Muffin())
//    Inventory.addItem(RearmChestsScroll())
    Inventory.addItem(MagicMapScroll())
    Inventory.addItem(MagicMapScroll())
    Inventory.addItem(MagicMapScroll())
    Inventory.addItem(MagicMapScroll())
    Inventory.addItem(MagicMapScroll())
    Inventory.addItem(MagicMapScroll())
//    Inventory.addItem(SmallHealthPotion())
//    Inventory.addItem(SmallHealthPotion())
//    Inventory.addItem(SmallHealthPotion())
//    Inventory.addItem(AttackScroll())
//    Inventory.addItem(DefenseScroll())
//    Inventory.addItem(SpeedPotion())
//    Inventory.addItem(RandomTeleportScroll())
    Inventory.addItem(SpawnMonsterScroll())
    Inventory.addItem(ExitTeleportScroll())
    Inventory.addItem(ExitTeleportScroll())
    Inventory.addItem(ExitTeleportScroll())
    Inventory.addItem(ExitTeleportScroll())
    Inventory.addItem(ExitTeleportScroll())
    Inventory.addItem(ExitTeleportScroll())
    
    SpellSelection.addItem(ShurikenSpell())
    SpellSelection.addItem(BallLightningSpell())
    SpellSelection.addItem(SpiralSpell())
//    SpellSelection.addItem(JumpSpell())
    
    Shop.addItem(ShurikenSpell())
    Shop.addItem(BallLightningSpell())
    Shop.addItem(SpiralSpell())
    Shop.addItem(JumpSpell())
    Shop.addItem(ExitTeleportScroll())
    
  }
  
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) {
    g.scale(4, 4)
    level.foreach(_.draw(player.pos.toVec2d, layer = 0))
    g.scale(0.5f, 0.5f)
    drawBeforePlayer.foreach(_.relativeDraw(player.pos, player.staticRenderPos))
    player.draw(player.staticRenderPos.x, player.staticRenderPos.y)
    drawAfterPlayer.foreach(_.relativeDraw(player.pos, player.staticRenderPos))
    effects.foreach(_.relativeDraw(player.pos, player.staticRenderPos))
    g.scale(2.0f, 2.0f)
    level.foreach(_.draw(player.pos.toVec2d, layer = 1))
    g.scale(0.5f, 0.5f)
    level.foreach(m => automap.draw(400 - m.width, 0))
    overlays.filter(_.active).foreach(_.draw)
    storyTexts.headOption.foreach(_.draw)
  }
  
  override def processKeys {
    super.processKeys
    if (level.isDefined) {
      if (!pressedKeys.isEmpty) pressedKeys.last match {
        case Input.KEY_I => if (!inStoryMode) setMenu(Some(Inventory))
        case Input.KEY_UP =>
          if (!inStoryMode) {
            currentMenu.getOrElse(player).go(Up)
          }
        case Input.KEY_DOWN =>
          if (!inStoryMode) {
            currentMenu.getOrElse(player).go(Down)
          }
        case Input.KEY_LEFT =>
          if (!inStoryMode) {
            currentMenu.getOrElse(player).go(Left)
          }
        case Input.KEY_RIGHT =>
          if (!inStoryMode) {
            currentMenu.getOrElse(player).go(Right)
          }
        case Input.KEY_S =>
          if (!inStoryMode) {
            setMenu(Some(SpellSelection))
          }
        case Input.KEY_U =>
          Audio.slash.stop
        case Input.KEY_SPACE => if (!inStoryMode) {
          if (currentMenu.isEmpty) player.attack else setMenu(None)
        }
        case Input.KEY_ESCAPE =>
          if (currentMenu.isEmpty) exitScene else setMenu(None)
        case Input.KEY_ENTER =>
          currentMenu.foreach { menu =>
            menu.activate(player, (i: GameItem) => {
              i.effect.foreach(e => GameEffects.apply(this, e, level.get, automap, player))
            })
          }
        case _ =>
      } else {
        player.stop
      }
    }
  }
}
