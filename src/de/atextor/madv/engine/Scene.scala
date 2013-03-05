package de.atextor.madv.engine

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import org.newdawn.slick.GameContainer
import org.newdawn.slick.state.BasicGameState
import org.newdawn.slick.state.StateBasedGame
import de.atextor.madv.game.Effect
import org.newdawn.slick.Input

abstract class Scene[PlayerType <: Entity] extends BasicGameState {
  var ticks: Int = 0
  var actions = ListBuffer[TimedAction]()
  var player: PlayerType
  var running = true
  val pressedKeys = Queue[Int]()  
  val entities: ListBuffer[Entity] = ListBuffer()
  val effects: ListBuffer[Effect] = ListBuffer()
  val overlays: ListBuffer[Overlay] = ListBuffer()
  val inventory = new Inventory
  
  def addEntity(e: Entity): ListBuffer[Entity] = entities += e
  def addEntities(e: Seq[Entity]): ListBuffer[Entity] = entities ++= e
  def addEffect(e: Effect): ListBuffer[Effect] = effects += e
  def addOverlay(o: Overlay): ListBuffer[Overlay] = overlays += o
  
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
    player.update(ticks)
    player.move
    entities.foreach { e =>
      e.enabled = e.distanceTo(player) < Constants.inactiveEntityDistance
      e.update(ticks)
      e.move
    }
    effects.foreach { e =>
      e.update(ticks)
      e.move
    }
    entities.filterNot(_.alive).foreach(entities -= _)
    effects.filterNot(_.alive).foreach(effects -= _)
    overlays.filterNot(_.alive).foreach(overlays -= _)
  }
  
  def processKeys
  
  def exitScene = running = false
  
  override def keyPressed(key: Int, c: Char) {
    pressedKeys += key
    processKeys
  }
  
  override def keyReleased(key: Int, c: Char) {
    super.keyReleased(key, c)
    pressedKeys.dequeueAll(_ == key)
    processKeys
  }
}
