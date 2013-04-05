package de.atextor.madv.game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.state.BasicGameState
import org.newdawn.slick.state.StateBasedGame
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.EntitySkin
import de.atextor.madv.engine.Hurt
import de.atextor.madv.engine.Slash
import de.atextor.madv.engine.Spellcast
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walk
import de.atextor.madv.engine.Scene
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.Entity
import de.atextor.madv.engine.LevelSetting
import de.atextor.madv.engine.UI
import de.atextor.madv.engine.Overlay
import scala.collection.mutable.ListBuffer
import de.atextor.madv.engine.StoryText
import de.atextor.madv.engine.TimedAction
import de.atextor.madv.engine.Action
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import de.atextor.madv.engine.TextBox

class TitleScreen(toggleFullscreen: () => Unit, startGame: () => Unit) extends BasicGameState {
  override val getID = 1
  
  def levelDecorations(l: Level): Seq[Entity] = Nil
  def levelTransformations(l: Level): Level = l
  def nextLevelSetting: LevelSetting = null
  val playerSkin: EntitySkin = Entities.playerSkin
  def win: Unit = {}
  
  val overlays: ListBuffer[Overlay] = ListBuffer()
  val storyTexts: ListBuffer[StoryText] = ListBuffer()
  var actions = ListBuffer[TimedAction]()
  var ticks: Int = 0
  
  def addOverlay(o: Overlay): ListBuffer[Overlay] = overlays += o
  def addStoryText(t: StoryText) = storyTexts += t
  
  def at(t: Duration, f: Action) { actions += ((t.toMillis.toInt, f)) }
  def in(t: Duration, f: Action) { actions += ((t.toMillis.toInt + ticks, f)) }
  
  def init(gc: GameContainer, game: StateBasedGame) {
    val img = UI.image("res/ui/title.png")
    val title = new Overlay(Vec2d(0, 0)) {
      def draw {
        img.draw
      }
    }
    addOverlay(title)
    Audio.music1.loop
    
    val intro = """|Eines Tages wollte Joan eine Wanderung machen.%Sie wollte durch den Taunus nach Wiesbaden.
                   |Im Alten Rathaus-Cafe in Hofheim kaufte sie einen Muffin.
                   |Plötzlich fing der Muffin an zu sprechen.
                   |"Hallo Joan!" sagte der Muffin.%Joan war baff. "Hallo Muffin."
                   |"Ich bin ein magischer Muffin! Deswegen kann ich sprechen%und noch andere tolle Sachen!"
                   |"Ich kann dir eine Abkürzung durch den Taunus zeigen" sagte der Muffin.
                   |"Es gibt unter dem Taunus ein Tunnel- und Höhlensystem,%von Frankfurt bis nach Wiesbaden."
                   |"Wenn du willst, zeige ich dir den Eingang."
                   |Joan überlegte. "Ist das nicht gefährlich?", fragte sie.
                   |"Nicht, wenn ich dir etwas meiner magischen Kraft abgebe",%sagte der Muffin.
                   |"Sieh dich aber vor, du wirst die Magie brauchen...%in den Höhlen gibts viele Böse Wesen."""".
      stripMargin.lines.map(_.replace("%", "\n")).zipWithIndex.foreach { case (l, i) =>
      val text = new TextBox(350, l, Vec2d(20, 220))
      in((i * 6) + 4 seconds, { t => addOverlay(text) })
      in(((i + 1) * 6) + 4 seconds, { t => text.alive = false })
    }
  }
  
  def update(gc: GameContainer, game: StateBasedGame, delta: Int) {
    ticks += delta
    var changed = false
    while (actions.size > 0 && actions.head._1 <= ticks) {
      actions.remove(0)._2(ticks)
      changed = true
    }
    if (changed) actions = actions.sortWith(_._1 < _._1)
    
    overlays.filterNot(_.alive).foreach(overlays -= _)
  }
  
  override def keyReleased(key: Int, c: Char) {
    startGame()
  }
  
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) {
    g.scale(2.0f, 2.0f)
    overlays.filter(_.active).foreach(_.draw)
    storyTexts.headOption.foreach(_.draw)
  }
  
  
  
}