package de.atextor.madv.game

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.future
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Input
import org.newdawn.slick.state.BasicGameState
import org.newdawn.slick.state.StateBasedGame
import de.atextor.madv.engine.Belt
import de.atextor.madv.engine.BlackCave
import de.atextor.madv.engine.Body
import de.atextor.madv.engine.Cell
import de.atextor.madv.engine.CellularAutomaton
import de.atextor.madv.engine.EntitySkin
import de.atextor.madv.engine.Feet
import de.atextor.madv.engine.Head
import de.atextor.madv.engine.Hurt
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.Slash
import de.atextor.madv.engine.Spellcast
import de.atextor.madv.engine.Torso
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walk
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.Vec
import de.atextor.madv.engine.Direction
import de.atextor.madv.engine.Up
import de.atextor.madv.engine.Left
import de.atextor.madv.engine.Right
import de.atextor.madv.engine.BlueCave

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
  
  var test: EntitySkin = null
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
  
  future {
    val cave = new CellularAutomaton.Rule(born = Set(6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
    val smooth = new CellularAutomaton.Rule(born = Set(5, 6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
    val ca = CellularAutomaton(50, 50).randomFill(0.5)(cave)(smooth)(smooth).addDeadBorder
//    implicit val caveDef = LavaCave
    implicit val caveDef = BlueCave
//    implicit val caveDef = BlackCave
    Level fromCellularAutomaton ca
  } onSuccess { case m => gameMap = Some(m) }
  
  def init(gc: GameContainer, game: StateBasedGame) {
    test = EntitySkin(List(Hurt, Slash, Spellcast, Walk),
        body = List(Body("female")),
        head = List(Head("female_darkblondehair")),
        torso = List(Torso("female_vest"), Torso("female_forestrobe")),
        belt = List(Belt("female_blackbelt"), Belt("female_ironbuckle")),
        feet = List(Feet("female_grayslippers")))
  }
  
  def draw(l: Level) = {
    l.draw(coord)
  }
  
  var playerDir: Direction = Down
  
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) {
    g.scale(4, 4)
    gameMap.foreach(draw(_))
    g.scale(0.5f, 0.5f)
    test.draw(playerDir, Walk, Vec2d(160, 80))
  }
  
  override def keyPressed(key: Int, c: Char) {
    val inc = 4
    super.keyPressed(key, c)
    if (key == Input.KEY_UP) {
      offset = Vec2d(offset.x, -inc)
      playerDir = Up
    } else if (key == Input.KEY_DOWN) {
      offset = Vec2d(offset.x, inc)
      playerDir = Down
    } else if (key == Input.KEY_LEFT) {
      offset = Vec2d(-inc, offset.y)
      playerDir = Left
    } else if (key == Input.KEY_RIGHT) {
      offset = Vec2d(inc, offset.y)
      playerDir = Right
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
