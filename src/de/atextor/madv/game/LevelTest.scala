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
import de.atextor.madv.engine.Dumb
import de.atextor.madv.engine.Humanoid
import de.atextor.madv.engine.InventoryItem
import de.atextor.madv.engine.MagicMapScroll
import de.atextor.madv.engine.StoryText
import de.atextor.madv.engine.Player
import de.atextor.madv.engine.GameEffects
import de.atextor.madv.engine.Chaser
import de.atextor.madv.engine.HealthDisplay
import de.atextor.madv.engine.Vec2f
import de.atextor.madv.engine.Projectile
import de.atextor.madv.engine.Shooter

class LevelTest(toggleFullscreen: () => Unit) extends Scene(toggleFullscreen) {
  override val getID = 1
  
  var player: Player = null
  var gameMap: Option[Level] = None
  var automap: AutoMap = null
  
  var orc: FemaleOrc = null
  
  def init(gc: GameContainer, game: StateBasedGame) {
//    implicit val caveDef = LavaCave
    implicit val caveDef = BlueCave
//    implicit val caveDef = BlackCave
//    val level = Level generateCoherentLevel
//    val level = Level generateStaticSmallLevel
    val level = Entities.placeChestInLevel(Level generateStaticSmallLevel, this)
//    val level = Entities.placeChestInLevel(Level generateCoherentLevel, this)
    
    val shooter = (pos: Vec2f) => new Projectile(spawner = player, visual = Entities.sparkle1, speed = 0.5f, damage = 20)
    val spell = new Shooter(shooter, 500 millis)
    
    val startCell = level.find(_.cell.properties contains Walkable).get
    player = new Player(level = level, startPosition = startCell.pos * 16, entitySkin = Entities.playerSkin)
    player.spell = Some(spell)
//    addEntities(Entities.placeEntitiesInLevel(player, level))
//    addEntities(entities)
    
    val orcStart = level.find(_.cell.properties contains Walkable, randomize = false).get
    orc = new FemaleOrc(level, player, new Chaser(player), orcStart.pos * 16)
    addEntity(orc)
    
    automap = new AutoMap(level, player) 
    
//    val startCell = m.exitLocation
//    player = new Player(level = m, startPosition = startCell + Down * 20, entitySkin = Entities.playerSkin)
    
    val hpDisplay = new HealthDisplay(player)
    addOverlay(hpDisplay)
    
    gameMap = Some(level)
    at(0 millis, t => player.stop)
    
    lazy val updateAm: Action = { t => automap.update(player); at(t.millis + 300.millis, updateAm) }
    at(0 millis, updateAm)
  }
  
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) {
    if (Constants.debug) {
      g.scale(2, 2)
      g.translate(90, 60)
    } else {
      g.scale(4, 4)
    }
    
    gameMap.foreach(_.draw(player.pos.toVec2d, layer = 0))
    g.scale(0.5f, 0.5f)
    drawBeforePlayer.foreach(_.relativeDraw(player.pos, player.staticRenderPos))
    player.draw(player.staticRenderPos.x, player.staticRenderPos.y)
    drawAfterPlayer.foreach(_.relativeDraw(player.pos, player.staticRenderPos))
    effects.foreach(_.relativeDraw(player.pos, player.staticRenderPos))
    g.scale(2.0f, 2.0f)
    gameMap.foreach(_.draw(player.pos.toVec2d, layer = 1))
    g.scale(0.5f, 0.5f)
    gameMap.foreach(m => automap.draw(400 - m.width, 0))
    overlays.filter(_.active).foreach(_.draw)
    storyTexts.headOption.foreach(_.draw)
  }
  
  override def processKeys {
    super.processKeys
    if (gameMap.isDefined) {
      if (!pressedKeys.isEmpty) pressedKeys.last match {
        case Input.KEY_I =>
          if (!inStoryMode) inventory.active = !inventory.active
        case Input.KEY_UP =>
          if (!inStoryMode) {
            if (inventory.active) inventory.changeSelection(Up) else player.go(Up)
          }
        case Input.KEY_DOWN =>
          if (!inStoryMode) {
            if (inventory.active) inventory.changeSelection(Down) else player.go(Down)
          }
        case Input.KEY_LEFT =>
          if (!inStoryMode) {
            if (!inventory.active) player.go(Left)
          }
        case Input.KEY_RIGHT =>
          if (!inStoryMode) {
            if (!inventory.active) player.go(Right)
          }
//        case Input.KEY_SPACE => addEffect(new Explosion(player.pos))
//        case Input.KEY_SPACE => orc.die
        case Input.KEY_SPACE => player.attack
        case Input.KEY_ESCAPE =>
          if (inventory.active) inventory.active = false else exitScene
        case Input.KEY_ENTER =>
          if (inventory.active) {
            inventory.activateSelected.foreach(item => GameEffects.apply(item.effect, gameMap.get, automap, None, player))
          }
        case _ =>
      } else {
        player.stop
      }
    }
  }
}
