package de.atextor.madv.engine

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue
import org.newdawn.slick.GameContainer
import org.newdawn.slick.state.BasicGameState
import org.newdawn.slick.state.StateBasedGame
import de.atextor.madv.game.Effect

abstract class Scene[PlayerType <: Entity] extends BasicGameState {
  var ticks: Int = 0
  var actions = ListBuffer[TimedAction]()
  var player: PlayerType
  val pressedKeys = Queue[Int]()  
  val entities: ListBuffer[Entity] = ListBuffer()
  val effects: ListBuffer[Effect] = ListBuffer()
  val texts: ListBuffer[Text] = ListBuffer()
  
  def addEntity(e: Entity): ListBuffer[Entity] = entities += e
  def addEntities(e: Seq[Entity]): ListBuffer[Entity] = entities ++= e
  def addEffect(e: Effect): ListBuffer[Effect] = effects += e
  def addText(t: Text): ListBuffer[Text] = texts += t
  
  def at(ticks: Int, f: Action) { actions += ((ticks, f)) }
  
  def update(gc: GameContainer, game: StateBasedGame, delta: Int) {
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
    texts.foreach(_.update(ticks))
    entities.filterNot(_.alive).foreach(entities -= _)
    effects.filterNot(_.alive).foreach(effects -= _)
  }
  
  def processKeys
  
  override def keyPressed(key: Int, c: Char) {
    super.keyPressed(key, c)
    pressedKeys += key
    processKeys
  }
  
  override def keyReleased(key: Int, c: Char) {
    super.keyReleased(key, c)
    pressedKeys.dequeueAll(_ == key)
    processKeys
  }
}
