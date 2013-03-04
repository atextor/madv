package de.atextor.madv.game

import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Input
import org.newdawn.slick.state.StateBasedGame
import de.atextor.madv.engine.AutoMap
import de.atextor.madv.engine.BlueCave
import de.atextor.madv.engine.Constants
import de.atextor.madv.engine.DoNothing
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.Left
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.Right
import de.atextor.madv.engine.Scene
import de.atextor.madv.engine.Text
import de.atextor.madv.engine.Up
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walkable
import de.atextor.madv.engine.Action
import de.atextor.madv.engine.Inventory
import de.atextor.madv.engine.Potion

class LevelTest extends Scene[Player] {
  override val getID = 1
  
  var player: Player = null
  var gameMap: Option[Level] = None
  var automap: AutoMap = null
  val inventory = new Inventory
  
  def init(gc: GameContainer, game: StateBasedGame) {
//    implicit val caveDef = LavaCave
    implicit val caveDef = BlueCave
//    implicit val caveDef = BlackCave
//    val level = Level generateCoherentLevel
    val level = Level generateStaticSmallLevel
    val startCell = level.find(_.cell.properties contains Walkable).get
    player = new Player(level = level, startPosition = startCell.pos * 16, entitySkin = Entities.playerSkin)
    addEntities(Entities.placeEntitiesInLevel(player, level))
    
    automap = new AutoMap(level, player) 
    
//    val startCell = m.exitLocation
//    player = new Player(level = m, startPosition = startCell + Down * 20, entitySkin = Entities.playerSkin)
    
    val chest = new Chest(player = player, startPos = player.pos + Vec2d(32, 0),
      onTouch = { t =>
        val text = new CenteredTextBox(width = 150, text = "Opened chest\nFound a potion!")
        addOverlay(text)
        at(t.milliseconds + 5.seconds, (_ => text.alive = false))
        inventory.addItem(new Potion())
      })
    addEntity(chest)
    
    gameMap = Some(level)
    at(0 millis, t => player.stop)
    
    lazy val updateAm: Action = { t => automap.update(player); at(t.millis + 300.millis, updateAm) }
    at(0 millis, updateAm)
    
    addOverlay(inventory)
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
    entities.foreach(_.relativeDraw(player.pos, player.staticRenderPos))
    player.draw(player.staticRenderPos.x, player.staticRenderPos.y)
    effects.foreach(_.relativeDraw(player.pos, player.staticRenderPos))
    g.scale(2.0f, 2.0f)
    gameMap.foreach(_.draw(player.pos, layer = 1))
    g.scale(0.5f, 0.5f)
    gameMap.foreach(m => automap.draw(400 - m.width, 0))
    
	g.setColor(org.newdawn.slick.Color.white);
    overlays.filter(_.active).foreach(_.draw)
  }
  
  def processKeys {
    if (gameMap.isDefined) {
      if (pressedKeys.size > 0) pressedKeys.last match {
        case Input.KEY_I => inventory.active = !inventory.active
        case Input.KEY_UP => if (inventory.active) inventory.changeSelection(Up) else player.go(Up)
        case Input.KEY_DOWN => if (inventory.active) inventory.changeSelection(Down) else player.go(Down)
        case Input.KEY_LEFT => if (!inventory.active) player.go(Left)
        case Input.KEY_RIGHT => if (!inventory.active) player.go(Right)
        case Input.KEY_SPACE => addEffect(new Explosion(player.pos))
        case Input.KEY_ESCAPE => if (inventory.active) inventory.active = false else exitScene
        case Input.KEY_ENTER => if (inventory.active) inventory.activateSelected
        case _ =>
      } else {
        player.stop
      }
    }
  }
}
