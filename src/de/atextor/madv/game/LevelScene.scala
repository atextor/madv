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

class LevelScene(toggleFullscreen: () => Unit) extends Scene(toggleFullscreen) {
  override val getID = 2
  val playerSkin = Entities.playerSkin
  
  val levelSettings = List(CoherentGreen, CoherentBlue, CoherentLava, IslandGreen, IslandLava, IslandBlack)
  val currentLevelSetting = levelSettings.iterator
  
  def win {
    val outro: Action = { ticks =>
      val lines = Source.fromFile("res/outro.txt").getLines.map(_.replace("|", "\n"))
      lines.zipWithIndex.foreach { case (l, i) =>
        val text = new TextBox(350, l, Vec2d(20, 180))
        in((i * 3) seconds, { t => addOverlay(text) })
        in(((i + 1) * 3) seconds, { t => text.alive = false })
      }
    }
    
    val text = new CenteredTextBox(width = 250, text = "Die Kiste enthielt:\nEinen Bandring")
    addOverlay(text)
    in(5 seconds, (_ => text.alive = false))
    addStoryText(new StoryText("Siehst du! War doch gar nicht so schwer.\nAuch wenn ich nicht erwartet hätte,\ndass du das schaffst", Some(Entities.muffinPortrait)))
    addStoryText(new StoryText("Wolltest du etwa, dass ich es\nnicht schaffe?\nWusstest du was hier auf\nuns wartet?", Some(Entities.heroPortrait)))
    addStoryText(new StoryText("Na klar! Hauptsache ich hatte\nmeinen Spass!", Some(Entities.muffinPortrait)))
    addStoryText(new StoryText("Den mach ich mir jetzt auch...\n*leckerer Muffin*\n*mampf*", Some(Entities.heroPortrait)))
    addStoryText(new StoryText("Ahhhh!", Some(Entities.muffinPortrait), onClose = () => {
      in(0 seconds, { t => outro(t) })
    }))
  }
  
  def nextLevelSetting = {
    Audio.slash.stop
    val setting = currentLevelSetting.next
    setting match {
      case CoherentGreen => 
      case CoherentBlue => 
        in(2 seconds, { t =>
          addStoryText(new StoryText("Es könnte sein, dass der Widerstand\nstärker wird. Pass auf!", Some(Entities.muffinPortrait)))
        })
      case CoherentLava =>
        Audio.music2.stop
        Audio.music3.loop
        in(2 seconds, { t =>
          addStoryText(new StoryText("Hier wird es heiss...", Some(Entities.heroPortrait)))
          addStoryText(new StoryText("Pass auf, dass ich nicht schmelze!", Some(Entities.muffinPortrait)))
        })
      case IslandGreen => 
        in(2 seconds, { t =>
          addStoryText(new StoryText("Hmm.. der Boden sieht hier\nziemlich zerklüftet aus.", Some(Entities.muffinPortrait)))
          addStoryText(new StoryText("Aber mit dem Sprung-Zauberspruch\nkein Problem!\nIch hoffe du hast noch genug Gold\nübrig\nHihihi!", Some(Entities.muffinPortrait)))
        })
      case IslandLava =>
        Audio.music3.stop
        Audio.music4.loop
      case IslandBlack => 
        in(2 seconds, { t =>
          addStoryText(new StoryText("Wir nähern uns dem Ende der Höhlen!\nHier ist ein grosser Schatz versteckt.\nDu musst nur zuerst die Untoten\nerledigen.", Some(Entities.muffinPortrait)))
          addStoryText(new StoryText("Und du schaust schön zu...", Some(Entities.heroPortrait)))
          addStoryText(new StoryText("Ja! Ist das nicht herrlich?", Some(Entities.muffinPortrait)))
          addStoryText(new StoryText("Pass auf, dass ich dich nicht esse.", Some(Entities.heroPortrait)))
        })
    }
    setting
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
    SpellSelection.addItem(ShurikenSpell())
    
    in(2 seconds, { t =>
      addStoryText(new StoryText("Ich habe dir die Fähigkeit verliehen,\ngefährliche Shurikens zu feuern.", Some(Entities.muffinPortrait)))
      addStoryText(new StoryText("Du kannst sie einfach aus deinen\nHänden beschwören.", Some(Entities.muffinPortrait)))
      addStoryText(new StoryText("Drücke 'S' um deine Zaubersprüche\nauszuwählen, und 'I' um\ndein Inventar zu öffnen.", Some(Entities.muffinPortrait)))
      addStoryText(new StoryText("Warum hilfst du mir?", Some(Entities.heroPortrait)))
      addStoryText(new StoryText("Ich wollte einfach mal was anderes\nsehen. Dummerweise habe ich...", Some(Entities.muffinPortrait)))
      addStoryText(new StoryText("keine Beine um die Höhlen\nselbst zu erforschen.", Some(Entities.muffinPortrait)))
      addStoryText(new StoryText("OK... schauen wir uns die Höhlen an.", Some(Entities.heroPortrait)))
      addStoryText(new StoryText("Und nicht vergessen: Mit der\nLeertaste zaubern!", Some(Entities.muffinPortrait)))
      addStoryText(new StoryText("Wenn du mich brauchst, ich bin\nin deinem Inventar.", Some(Entities.muffinPortrait)))
    })
    
    Shop.addItem(ShurikenSpell())
    Shop.addItem(BallLightningSpell())
    Shop.addItem(SpiralSpell())
    Shop.addItem(JumpSpell())
    Shop.addItem(ExitTeleportScroll())
    Shop.addItem(SmallHealthPotion())
    
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
