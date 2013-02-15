package de.atextor.madv.game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Input
import org.newdawn.slick.state.StateBasedGame

import de.atextor.madv.engine.CellularAutomaton
import de.atextor.madv.engine.Constants
import de.atextor.madv.engine.DoNothing
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.LavaCave
import de.atextor.madv.engine.BlueCave
import de.atextor.madv.engine.Left
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.Right
import de.atextor.madv.engine.Scene
import de.atextor.madv.engine.Up
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walkable
import de.atextor.madv.engine.Audio

class LevelTest extends Scene[Player] {
  override val getID = 1
  
  var player: Player = null
  var gameMap: Option[Level] = None
  
  def init(gc: GameContainer, game: StateBasedGame) {
//    implicit val caveDef = LavaCave
    implicit val caveDef = BlueCave
//    implicit val caveDef = BlackCave
    val level = Level generateCoherentLevel
//    val level = Level generateStaticSmallLevel
    val startCell = level.find(_.cell.properties contains Walkable).get
    player = new Player(level = level, startPosition = startCell.pos * 16, entitySkin = Entities.playerSkin)
    addEntities(Entities.placeEntitiesInLevel(player, level))
//    val startCell = m.exitLocation
//    player = new Player(level = m, startPosition = startCell + Down * 20, entitySkin = Entities.playerSkin)
    val chest = new Chest(player = player, startPos = player.pos + Vec2d(32, 0), onTouch = DoNothing)
    addEntity(chest)
      
    gameMap = Some(level)
    at(0, t => player.stop)
  }
  
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) {
    if (Constants.debug) {
      g.scale(2, 2)
      g.translate(90, 60)
    } else {
      g.scale(4, 4)
    }
    
    gameMap.foreach(_.draw(player.pos, layer = 0))
    g.scale(0.5f, 0.5f)
    entities.foreach(e => if (e.enabled) e.draw((e.pos.x - player.pos.x) * 2 + player.staticRenderPos.x + 8, (e.pos.y - player.pos.y) * 2 + player.staticRenderPos.y + 32))
    player.draw(player.staticRenderPos.x, player.staticRenderPos.y)
    g.scale(2.0f, 2.0f)
    gameMap.foreach(_.draw(player.pos, layer = 1))
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
