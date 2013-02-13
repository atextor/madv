package de.atextor.madv.game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Input
import org.newdawn.slick.state.StateBasedGame
import de.atextor.madv.engine.CellularAutomaton
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.Left
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.Right
import de.atextor.madv.engine.Scene
import de.atextor.madv.engine.Up
import de.atextor.madv.engine.Walkable
import de.atextor.madv.engine.LavaCave
import de.atextor.madv.engine.Constants

class LevelTest extends Scene {
  override val getID = 1
  
  var player: Player = null
  var gameMap: Option[Level] = None
  
  def generateLevel: Level = {
    val area = CellularAutomaton.generateCoherentLevel
//    val area = CellularAutomaton.staticSmallLevel
    implicit val caveDef = LavaCave
//    implicit val caveDef = BlueCave
//    implicit val caveDef = BlackCave
    Level.fromCellularAutomaton(area)
  }
  
  def init(gc: GameContainer, game: StateBasedGame) {
    val m = generateLevel
    val startCell = m.find(_.cell.properties contains Walkable).get
    player = new Player(level = m, startPosition = startCell.pos * 16, entitySkin = Entities.playerSkin)
    addEntity(player)
    gameMap = Some(m)
    at(0, t => player.stop)
  }
  
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) {
    if (Constants.debug) {
      g.scale(2, 2)
      gameMap.foreach(_.draw(player.pos, layer = 0))
      gameMap.foreach(_.draw(player.pos, layer = 1))
    } else {
      g.scale(4, 4)
      gameMap.foreach(_.draw(player.pos, layer = 0))
      g.scale(0.5f, 0.5f)
      entities.foreach(_.draw)
      g.scale(2.0f, 2.0f)
      gameMap.foreach(_.draw(player.pos, layer = 1))
    }
  }
  
  def processKeys {
    if (gameMap.isDefined) {
      if (pressedKeys.size > 0) pressedKeys.last match {
        case Input.KEY_UP => player.go(Up)
        case Input.KEY_RIGHT => player.go(Right)
        case Input.KEY_DOWN => player.go(Down)
        case Input.KEY_LEFT => player.go(Left)
        case _ =>
      } else {
        player.stop
      }
    }
  }
}
