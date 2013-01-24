package de.atextor.madv.game

import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.future
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import org.newdawn.slick.SpriteSheet
import org.newdawn.slick.state.BasicGameState
import org.newdawn.slick.state.StateBasedGame
import de.atextor.madv.engine.Cell
import de.atextor.madv.engine.CellularAutomaton
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.Vec2d
import org.newdawn.slick.Input
import scala.concurrent.Await
import scala.util.Success
import de.atextor.madv.engine.LevelCell

class LevelTest extends BasicGameState {
  override val getID = 1
  
  val f: Future[String] = future {
    "hallo"
  }
  
  def printGrid(g: CellularAutomaton) {
    for (y <- 0 to g.height - 1) {
      for (x <- 0 to g.width - 1) {
        print (if (g.isAlive(Cell(x, y))) "#" else ".")
      }
      println
    }
  }
  
  
  var coord = Vec2d(64, 64)
  var offset = Vec2d(0, 0)
  var gameMap: Option[Level] = None
  
  
  val gameMap2: Level = {
    val allCells = CellularAutomaton(10, 10).allCells.toSet
    val island = ((for (x <- 0 until 10; y <- 0 until 10) yield (x, y)) collect {
      case (x, y) if (math.sqrt((x - 5) * (x - 5) + (y - 5) * (y - 5)) > 3) => Cell(x, y)
    }).toSet
    val ca = CellularAutomaton(width = 10, height = 10, liveCells = allCells -- island)
    Level fromCellularAutomaton ca
  }
  
//  val mapFuture: Future[Level] = future {
  future {
    val cave = new CellularAutomaton.Rule(born = Set(6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
    val smooth = new CellularAutomaton.Rule(born = Set(5, 6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
    val ca = CellularAutomaton(50, 50).randomFill(0.5)(cave)(smooth)(smooth).addDeadBorder
    Level fromCellularAutomaton ca
  } onSuccess { case m => gameMap = Some(m) }
  
  def init(gc: GameContainer, game: StateBasedGame) {
//    mapFuture onSuccess { case m => gameMap = Some(m) }
  }
  
  def draw(l: Level) = {
    l.draw(coord)
  }
  
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) {
    g.scale(4, 4)
    gameMap.foreach(draw(_))
  }
  
  override def keyPressed(key: Int, c: Char) {
    val inc = 4
    super.keyPressed(key, c)
    if (key == Input.KEY_UP) {
      offset = Vec2d(offset.x, -inc)
    } else if (key == Input.KEY_DOWN) {
      offset = Vec2d(offset.x, inc)
    } else if (key == Input.KEY_LEFT) {
      offset = Vec2d(-inc, offset.y)
    } else if (key == Input.KEY_RIGHT) {
      offset = Vec2d(inc, offset.y)
    }
  }
  
  override def keyReleased(key: Int, c: Char) {
    super.keyReleased(key, c)
    if (key == Input.KEY_UP) {
      offset = Vec2d(offset.x, 0)
    } else if (key == Input.KEY_DOWN) {
      offset = Vec2d(offset.x, 0)
    } else if (key == Input.KEY_LEFT) {
      offset = Vec2d(0, offset.y)
    } else if (key == Input.KEY_RIGHT) {
      offset = Vec2d(0, offset.y)
    }
  }
  
  def update(gc: GameContainer, game: StateBasedGame, delta: Int) {
    coord += offset
  }
}
