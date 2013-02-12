package de.atextor.madv.game

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.future
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Input
import org.newdawn.slick.state.StateBasedGame
import de.atextor.madv.engine.BlackCave
import de.atextor.madv.engine.Cell
import de.atextor.madv.engine.CellularAutomaton
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.Humanoid
import de.atextor.madv.engine.Left
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.Right
import de.atextor.madv.engine.Scene
import de.atextor.madv.engine.Up
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walk
import de.atextor.madv.engine.Walkable
import de.atextor.madv.engine.Direction
import de.atextor.madv.engine.EntitySkin

class LevelTest extends Scene {
  override val getID = 1
  
  class Player(level: Level, startPosition: Vec2d, skin: EntitySkin) extends Humanoid (
      skin = skin,
      spriteAction = Walk,
      startPosition = startPosition,
      speed = 1
  ) {
    override def draw = skin.draw(lookingDirection, spriteAction, Vec2d(173, 85))
    
    override def move = {
      super.move
      if (level.tileAt(pos).properties contains Walkable) {
        true 
      } else {
        pos += movingDirection.invert * speed 
        false
      }
    }
    
    override def stop = {
      super.stop
      skin.stopAnimation(lookingDirection, spriteAction)
    }
    
    override def go(d: Direction) = {
      super.go(d)
      skin.startAnimation(lookingDirection, spriteAction)
    }
  }
  
  var player: Player = null
  var gameMap: Option[Level] = None
  
  val gameMap2: Level = {
    val allCells = CellularAutomaton(10, 10).allCells.toSet
    val island = ((for (x <- 0 until 10; y <- 0 until 10) yield (x, y)) collect {
      case (x, y) if (math.sqrt((x - 5) * (x - 5) + (y - 5) * (y - 5)) > 3) => Cell(x, y)
    }).toSet
    val ca = CellularAutomaton(width = 10, height = 10, liveCells = allCells -- island)
    Level fromCellularAutomaton ca
  }
  
  val levelFuture: Future[Level] = future {
    val cave = new CellularAutomaton.Rule(born = Set(6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
    val smooth = new CellularAutomaton.Rule(born = Set(5, 6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
    val ca = CellularAutomaton(40, 40).randomFill(0.4).upscale(smooth)(smooth)(smooth).addDeadBorder.fixPotholes
    val area = ca.copy(liveCells = ca.sortAreasBySize(ca.areas).last)
//    implicit val caveDef = LavaCave
//    implicit val caveDef = BlueCave
//    implicit val caveDef = BlackCave
    Level fromCellularAutomaton area
  }
  
  def init(gc: GameContainer, game: StateBasedGame) {
    levelFuture onSuccess { case m =>
      val startCell = m.find(_.properties contains Walkable, randomize = true).get
      player = new Player(level = m, startPosition = startCell.pos, skin = Entities.playerSkin)
      addEntity(player)
      gameMap = Some(m)
      at(0, t => player.stop)
    }
  }
  
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) {
    g.scale(4, 4)
    gameMap.foreach(_.draw(player.pos, layer = 0))
    g.scale(0.5f, 0.5f)
    entities.foreach(_.draw)
    g.scale(2.0f, 2.0f)
    gameMap.foreach(_.draw(player.pos, layer = 1))
  }
  
  def processKeys {
    if (pressedKeys.size > 0 && gameMap.isDefined) pressedKeys.last match {
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
