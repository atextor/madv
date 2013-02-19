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
import de.atextor.madv.engine.AutoMap
import de.atextor.madv.engine.Action
import de.atextor.madv.engine.Text
import org.newdawn.slick.UnicodeFont
import java.awt.Font
import org.newdawn.slick.font.effects.ColorEffect
import org.newdawn.slick.font.effects.ShadowEffect
import org.newdawn.slick.Color
import de.atextor.madv.engine.Inventory

class LevelTest extends Scene[Player] {
  override val getID = 1
  
  var player: Player = null
  var gameMap: Option[Level] = None
  var automap: AutoMap = null
  
  def init(gc: GameContainer, game: StateBasedGame) {
//    implicit val caveDef = LavaCave
    implicit val caveDef = BlueCave
//    implicit val caveDef = BlackCave
    val level = Level generateCoherentLevel
//    val level = Level generateStaticSmallLevel
    val startCell = level.find(_.cell.properties contains Walkable).get
    player = new Player(level = level, startPosition = startCell.pos * 16, entitySkin = Entities.playerSkin)
    addEntities(Entities.placeEntitiesInLevel(player, level))
    
    automap = new AutoMap(level, player) 
    
//    val startCell = m.exitLocation
//    player = new Player(level = m, startPosition = startCell + Down * 20, entitySkin = Entities.playerSkin)
    val chest = new Chest(player = player, startPos = player.pos + Vec2d(32, 0), onTouch = DoNothing)
    addEntity(chest)
    addText(new Text("Hallo Wörld", appear = true))
    
    gameMap = Some(level)
    at(0, t => player.stop)
    
    lazy val updateAm: Action = { t => automap.update(player); at(t + 300, updateAm) }
    at(0, updateAm)
  }
  
  var ui = new Inventory(Vec2d(100, 100))
  
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
    
    ui.draw(10, 10)
    
	g.setColor(org.newdawn.slick.Color.white);
    texts.foreach(_.draw(10, 10))
  }
  
  def processKeys {
    if (gameMap.isDefined) {
      if (pressedKeys.size > 0) pressedKeys.last match {
        case Input.KEY_UP => player.go(Up)
        case Input.KEY_RIGHT => player.go(Right)
        case Input.KEY_DOWN => player.go(Down)
        case Input.KEY_LEFT => player.go(Left)
        case Input.KEY_SPACE => addEffect(new Explosion(player.pos))
        case _ =>
      } else {
        player.stop
      }
    }
  }
}
