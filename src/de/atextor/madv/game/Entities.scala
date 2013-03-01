package de.atextor.madv.game

import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import org.newdawn.slick.Animation
import org.newdawn.slick.SpriteSheet
import de.atextor.madv.engine.Action
import de.atextor.madv.engine.Audio
import de.atextor.madv.engine.Constants
import de.atextor.madv.engine.DoNothing
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.Entity
import de.atextor.madv.engine.EntitySkin
import de.atextor.madv.engine.FrameBox
import de.atextor.madv.engine.Hurt
import de.atextor.madv.engine.Left
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.PartName.belt
import de.atextor.madv.engine.PartName.body
import de.atextor.madv.engine.PartName.feet
import de.atextor.madv.engine.PartName.head
import de.atextor.madv.engine.PartName.torso
import de.atextor.madv.engine.Right
import de.atextor.madv.engine.SimpleSprite
import de.atextor.madv.engine.Slash
import de.atextor.madv.engine.Spellcast
import de.atextor.madv.engine.SpriteAnimation
import de.atextor.madv.engine.Text
import de.atextor.madv.engine.Up
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walk
import de.atextor.madv.engine.Walkable
import de.atextor.madv.engine.noArg2intArg
import de.atextor.madv.engine.Overlay
import org.newdawn.slick.Graphics

object Entities {
  private def animation(sheet: String, sizeX: Int, frames: Int, delay: Duration, sizeY: Int = 0) =
    SpriteAnimation(new SpriteSheet(sheet, sizeX, if (sizeY == 0) sizeX else sizeY), new SimpleSprite(frames, delay), 0)
  lazy val playerSkin = EntitySkin(Vec2d(64, 64), List(Hurt, Slash, Spellcast, Walk),
     (body  -> ("female" :: Nil)),
     (head  -> ("female_darkblondehair" :: Nil)),
     (torso -> ("female_vest" :: "female_forestrobe" :: Nil)),
     (belt  -> ("female_blackbelt" :: "female_ironbuckle" :: Nil)),
     (feet  -> ("female_grayslippers" :: Nil)))
  lazy val goldCoinSprite = animation(sheet = "res/items/coin_gold.png", sizeX = 32, frames = 8, delay = 60 millis)
  lazy val silverCoinSprite = animation(sheet = "res/items/coin_silver.png", sizeX = 32, frames = 8, delay = 60 millis)
  lazy val copperCoinSprite = animation(sheet = "res/items/coin_copper.png", sizeX = 32, frames = 8, delay = 60 millis)
  lazy val chestSprite = animation(sheet = "res/items/chest.png", sizeX = 32, frames = 2, delay = 1 second)
  lazy val sparkle1 = animation(sheet = "res/effects/sparkle1.png", sizeX = 31, frames = 8, delay = 120 millis)
  lazy val explosionSheet = new SpriteSheet("res/effects/explosion.png", 57, 57)
  def explosion = SpriteAnimation(explosionSheet, new SimpleSprite(frames = 10, delay = 100 millis), 0)
  
  def placeEntitiesInLevel(player: Player, level: Level): Seq[Entity] = {
    import level.PlacedLevelCell
    
    val playPling: Action = if (Constants.debug) DoNothing else (Audio.pling.play _)
    
    // Place a bunch of coins at the end of land
    val landsEndProperty: PlacedLevelCell => Boolean = { c =>
      (c.cell.properties.contains(Walkable)) &&
      ((c + Up).cell.properties.contains(Walkable)) &&
      ((c + Down).cell.properties.contains(Walkable)) &&
      ((c + Left).cell.properties.contains(Walkable)) &&
      ((c + Right).cell.properties.contains(Walkable)) &&
      (List(((c + Up * 2).cell.properties.contains(Walkable)),
            ((c + Down * 2).cell.properties.contains(Walkable)),
            ((c + Left * 2).cell.properties.contains(Walkable)),
            ((c + Right * 2).cell.properties.contains(Walkable))).filter(identity).size == 1)
    }
    val coins = level.placedCells.filter(landsEndProperty).flatMap { pc =>
      new GoldCoin(player, pc.pos * 16, onTouch = playPling) ::
      new SilverCoin(player, (pc.pos + Up) * 16, onTouch = playPling) ::
      new SilverCoin(player, (pc.pos + Down) * 16, onTouch = playPling) ::
      new SilverCoin(player, (pc.pos + Right) * 16, onTouch = playPling) ::
      new SilverCoin(player, (pc.pos + Left) * 16, onTouch = playPling) :: Nil
    }
    
    /*
    // Coins on every walkable cell
    val coins = level.placedCells.filter(_.cell.properties contains Walkable).map { pc =>
      new GoldCoin(player, pc.pos * 16, onTouch = playPling)
    }
    */
    coins
  }
}

class Chest(player: Player, startPos: Vec2d, onTouch: Action) extends
    Entity(size = Vec2d(32, 32), visual = Some(Entities.chestSprite), pos = startPos) {
  visual.get.stop
  var activated = false
  def tick(delta: Int) = {
    if (player touches this) {
      if (!activated) {
        activated = true
        Audio.chestopen.play
        visual.get.setCurrentFrame(1)
        onTouch(delta)
      }
      player.goBack
    } 
  }
}

class Collectible(player: Player, startPos: Vec2d, onTouch: Action, visual: Animation) extends 
    Entity(size = Vec2d(visual.getWidth, visual.getHeight), visual = Some(visual), pos = startPos) {
  def tick(delta: Int) = {
    if (player touches this) {
      alive = false
      onTouch(delta)
    }
  }
}

class Effect(startPos: Vec2d, visual: Animation) extends
    Entity(size = Vec2d(visual.getWidth, visual.getHeight), visual = Some(visual), pos = startPos) {
  visual.setLooping(false)
  def tick(delta: Int) = {
    if (visual.getFrame == visual.getFrameCount - 1) {
      alive = false
    }
  } 
}

class GoldCoin(player: Player, startPos: Vec2d, onTouch: Action) extends Collectible(player, startPos, onTouch, Entities.goldCoinSprite)
class SilverCoin(player: Player, startPos: Vec2d, onTouch: Action) extends Collectible(player, startPos, onTouch, Entities.silverCoinSprite)
class CopperCoin(player: Player, startPos: Vec2d, onTouch: Action) extends Collectible(player, startPos, onTouch, Entities.copperCoinSprite)

class Explosion(startPos: Vec2d) extends Effect(startPos, Entities.explosion)

class TextBox(width: Int, text: String) extends Overlay(pos = Vec2d(200 - width / 2, 30)) {
  val size = Vec2d(width, Text.getTextHeight(text))
  val box = new FrameBox(size)
  val txt = new Text(text)
  val tw = Text.unicodeFont.getWidth(text)
  
  override def draw {
    box.draw(pos.x, pos.y)
    txt.draw(pos.x + width / 2 - tw / 2, pos.y + 7)
  }
  def tick(delta: Int) {}
}
