package de.atextor.madv.engine

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Input
import org.newdawn.slick.state.BasicGameState
import org.newdawn.slick.state.StateBasedGame
import de.atextor.madv.game.Effect

abstract class Scene(toggleFullscreen: () => Unit) extends BasicGameState {
  var ticks: Int = 0
  var actions = ListBuffer[TimedAction]()
  var player: Player
  var running = true
  var currentMenu: Option[Menu] = None
  val pressedKeys = Queue[Int]()  
  val entities: ListBuffer[Entity] = ListBuffer()
  val effects: ListBuffer[Effect] = ListBuffer()
  val overlays: ListBuffer[Overlay] = ListBuffer()
  val storyTexts: ListBuffer[StoryText] = ListBuffer()
  
  val menus = List(Inventory, SpellSelection)
  menus.foreach(addOverlay(_))
  
  var drawBeforePlayer: Seq[Entity] = Seq()
  var drawAfterPlayer: Seq[Entity] = Seq()
  
  def inStoryMode = !(storyTexts.isEmpty)
  def addEffect(e: Effect): ListBuffer[Effect] = effects += e
  def addOverlay(o: Overlay): ListBuffer[Overlay] = overlays += o
  def addStoryText(t: StoryText) = storyTexts += t
  def addEntity(e: Entity): ListBuffer[Entity] = entities += e
  def addEntities(e: Seq[Entity]): ListBuffer[Entity] = entities ++= e
  def setMenu(m: Option[Menu]) = m match {
    case Some(target) =>
      currentMenu.foreach(_.active = false)
      if (m == currentMenu) {
        currentMenu = None
      } else {
        currentMenu = m
        currentMenu.foreach(_.active = true)
      }
    case None =>
      currentMenu.foreach(_.active = false)
      currentMenu = None
  }
  
  def at(ticks: Duration, f: Action) { actions += ((ticks.toMillis.toInt, f)) }
  
  def update(gc: GameContainer, game: StateBasedGame, delta: Int) {
    if (!running) gc.exit
    Text.unicodeFont.loadGlyphs
    
    ticks += delta
    var changed = false
    while (actions.size > 0 && actions.head._1 <= ticks) {
      actions.remove(0)._2(ticks)
      changed = true
    }
    if (changed) actions = actions.sortWith(_._1 < _._1)
    
    if (currentMenu.isEmpty) {
      player.update(this, ticks)
      player.move
      
      entities.foreach { e =>
        e.enabled = e.distanceTo(player) < Constants.inactiveEntityDistance
        e.update(this, ticks)
        e.move
      }
      
      effects.foreach { e =>
        e.update(this, ticks)
        e.move
      }
      storyTexts.headOption.foreach(_.update(this, ticks))
      entities.filterNot(_.alive).foreach(entities -= _)
      effects.filterNot(_.alive).foreach(effects -= _)
      overlays.filterNot(_.alive).foreach(overlays -= _)
      storyTexts.filterNot(_.alive).foreach(storyTexts -= _)
      
      if (!player.alive) {
        addStoryText(new StoryText("You Die.", None))
      }
    
      // do Z ordering for all entities that need to be drawn
      val parted = entities.filter(_.enabled).sortWith(_.pos.y < _.pos.y).partition(_.pos.y < player.pos.y)
      drawBeforePlayer = parted._1
      drawAfterPlayer = parted._2
    }
  }
  
  def processKeys {
    if (inStoryMode && pressedKeys.size > 0) pressedKeys.last match {
      case Input.KEY_SPACE => storyTexts.head.trigger
      case Input.KEY_ESCAPE => storyTexts.head.alive = false
      case _ =>
    }
  }
  
  def exitScene = running = false
  
  override def keyPressed(key: Int, c: Char) {
    if (key == Input.KEY_F12) {
      toggleFullscreen()
    } else {
      pressedKeys += key
      processKeys
    }
  }
  
  override def keyReleased(key: Int, c: Char) {
    super.keyReleased(key, c)
    pressedKeys.dequeueAll(_ == key)
    processKeys
  }
}
