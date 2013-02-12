package de.atextor.madv.engine

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue

import org.newdawn.slick.GameContainer
import org.newdawn.slick.state.BasicGameState
import org.newdawn.slick.state.StateBasedGame

abstract class Scene extends BasicGameState {
  var actions = ListBuffer[TimedAction]()
  val pressedKeys = Queue[Int]()  
  val entities: ListBuffer[Entity] = ListBuffer()
  
  def addEntity(e: Entity): ListBuffer[Entity] = entities += e
  
  def at(ticks: Int, f: Action) { actions += ((ticks, f)) }
  
  def update(gc: GameContainer, game: StateBasedGame, ticks: Int) {
    var changed = false
    while (actions.size > 0 && actions.head._1 <= ticks) {
      actions.remove(0)._2(ticks)
      changed = true
    }
    if (changed) actions = actions.sortWith(_._1 < _._1)
    entities.foreach(_.tick(ticks))
    entities.foreach(_.move)
    entities.filterNot(_.alive).foreach(entities -= _)
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