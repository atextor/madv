package de.atextor.madv.game

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.future
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Input
import org.newdawn.slick.state.BasicGameState
import org.newdawn.slick.state.StateBasedGame
import de.atextor.madv.engine.Cell
import de.atextor.madv.engine.CellularAutomaton
import de.atextor.madv.engine.Direction
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.Entity
import de.atextor.madv.engine.Left
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.Right
import de.atextor.madv.engine.Up
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Scene
import de.atextor.madv.engine.Humanoid
import de.atextor.madv.engine.Walk

class LevelTest extends Scene {
  override val getID = 1
  
  val player = new Humanoid (
      skin = Entities.playerSkin,
      spriteAction = Walk,
      startPosition = Vec2d(160, 90),
      speed = 10
  ) {
    override def draw = {}//skin.draw(lookingDirection, spriteAction, Vec2d(160, 90))
  }
  
  var gameMap: Option[Level] = None
  
  val gameMap2: Level = {
    val allCells = CellularAutomaton(10, 10).allCells.toSet
    val island = ((for (x <- 0 until 10; y <- 0 until 10) yield (x, y)) collect {
      case (x, y) if (math.sqrt((x - 5) * (x - 5) + (y - 5) * (y - 5)) > 3) => Cell(x, y)
    }).toSet
    val ca = CellularAutomaton(width = 10, height = 10, liveCells = allCells -- island)
    Level fromCellularAutomaton ca
  }
  
  future {
    val cave = new CellularAutomaton.Rule(born = Set(6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
    val smooth = new CellularAutomaton.Rule(born = Set(5, 6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
    val ca = CellularAutomaton(40, 40).randomFill(0.4).upscale(smooth)(smooth)(smooth).addDeadBorder.fixPotholes
    val area = ca.copy(liveCells = ca.sortAreasBySize(ca.areas).last)
//    implicit val caveDef = LavaCave
//    implicit val caveDef = BlueCave
//    implicit val caveDef = BlackCave
    Level fromCellularAutomaton area
  } onSuccess { case m => gameMap = Some(m) }
  
  def init(gc: GameContainer, game: StateBasedGame) {
    entities += player
  }
  
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) {
    g.scale(4, 4)
    gameMap.foreach(l => l.draw(player.pos, layer = 0))
    g.scale(0.5f, 0.5f)
    entities.foreach(_.draw)
    g.scale(2.0f, 2.0f)
    gameMap.foreach(l => l.draw(player.pos, layer = 1))
  }
  
  def processKeys {
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
