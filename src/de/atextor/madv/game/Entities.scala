package de.atextor.madv.game

import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import org.newdawn.slick.Animation
import org.newdawn.slick.SpriteSheet
import de.atextor.madv.engine.Audio
import de.atextor.madv.engine.Brain
import de.atextor.madv.engine.Constants
import de.atextor.madv.engine.Direction
import de.atextor.madv.engine.NoAction
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.Entity
import de.atextor.madv.engine.EntitySkin
import de.atextor.madv.engine.Humanoid
import de.atextor.madv.engine.Hurt
import de.atextor.madv.engine.Left
import de.atextor.madv.engine.Level
import de.atextor.madv.engine.LevelCell
import de.atextor.madv.engine.Overlay
import de.atextor.madv.engine.PartName.belt
import de.atextor.madv.engine.PartName.body
import de.atextor.madv.engine.PartName.feet
import de.atextor.madv.engine.PartName.head
import de.atextor.madv.engine.PartName.torso
import de.atextor.madv.engine.PartName.weapon
import de.atextor.madv.engine.Player
import de.atextor.madv.engine.Right
import de.atextor.madv.engine.Scene
import de.atextor.madv.engine.SimpleSprite
import de.atextor.madv.engine.Slash
import de.atextor.madv.engine.Spellcast
import de.atextor.madv.engine.SpriteAnimation
import de.atextor.madv.engine.TextBox
import de.atextor.madv.engine.UI
import de.atextor.madv.engine.Up
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Vec2f
import de.atextor.madv.engine.Walk
import de.atextor.madv.engine.Walkable
import de.atextor.madv.engine.noArg2intArg
import de.atextor.madv.engine.Action
import de.atextor.madv.engine.Inventory
import de.atextor.madv.engine.IsGoodRearmable
import de.atextor.madv.engine.IsCollectible

object Entities {
  private def animation(sheet: String, sizeX: Int, frames: Int, delay: Duration, sizeY: Int = 0) =
    SpriteAnimation(new SpriteSheet(sheet, sizeX, if (sizeY == 0) sizeX else sizeY), new SimpleSprite(frames, delay), 0)
    
  lazy val playerSkin = EntitySkin(Vec2d(64, 64), List(Hurt, Slash, Spellcast, Walk),
     (body  -> ("female" :: Nil)),
     (head  -> ("female_darkblondehair" :: Nil)),
     (torso -> ("female_vest" :: "female_forestrobe" :: Nil)),
     (belt  -> ("female_blackbelt" :: "female_ironbuckle" :: Nil)),
     (feet  -> ("female_grayslippers" :: Nil)))
  lazy val femaleOrcSkin = EntitySkin(Vec2d(64, 64), List(Hurt, Slash, Spellcast, Walk),
     (body  -> ("female_orc" :: Nil)),
     (torso -> ("female_leather_torso" :: "female_leather_shoulders" :: Nil)),
     (belt  -> ("female_brownbelt" :: Nil)),
     (weapon -> ("dagger" :: Nil)))
  lazy val femaleArmoredOrcSkin = EntitySkin(Vec2d(64, 64), List(Hurt, Slash, Spellcast, Walk),
     (body  -> ("female_orc" :: Nil)),
     (torso -> ("female_chainmail" :: "female_plate_shoulders" :: Nil)),
     (belt  -> ("female_ironbelt" :: Nil)))
  lazy val femaleHeavyArmoredOrcSkin = EntitySkin(Vec2d(64, 64), List(Hurt, Slash, Spellcast, Walk),
     (body  -> ("female_orc" :: Nil)),
     (torso -> ("female_plate_mail" :: "female_plate_shoulders" :: Nil)),
     (feet  -> ("female_plate_boots" :: "female_plate_greaves" :: Nil)),
     (belt  -> ("female_ironbelt" :: Nil)))
     
  // Shared between all entities using this visual
  lazy val goldCoinSprite = animation(sheet = "res/items/coin_gold.png", sizeX = 32, frames = 8, delay = 60 millis).get
  lazy val silverCoinSprite = animation(sheet = "res/items/coin_silver.png", sizeX = 32, frames = 8, delay = 60 millis).get
  lazy val copperCoinSprite = animation(sheet = "res/items/coin_copper.png", sizeX = 32, frames = 8, delay = 60 millis).get
  lazy val sparkle1 = animation(sheet = "res/effects/sparkle1.png", sizeX = 31, frames = 8, delay = 120 millis).get
  lazy val spiral = animation(sheet = "res/effects/spiral.png", sizeX = 31, frames = 16, delay = 70 millis).get
  lazy val star1 = animation(sheet = "res/effects/star1.png", sizeX = 31, frames = 8, delay = 120 millis).get
  lazy val muffinPortrait = UI.image("res/portraits/muffin.png")
  lazy val heroPortrait = UI.image("res/portraits/hero.png")
  
  // Each entity has its own visual
  lazy val chestSheet = new SpriteSheet("res/items/chest.png", 32, 32)
  def chestSprite = SpriteAnimation(chestSheet, new SimpleSprite(frames = 2, delay = 1 second), 0).get
  lazy val shurikenSheet = new SpriteSheet("res/effects/shuriken.png", 31, 31)
  def shuriken = SpriteAnimation(shurikenSheet, new SimpleSprite(frames = 8, delay = 120 millis), 0).get
  lazy val explosionSheet = new SpriteSheet("res/effects/explosion.png", 57, 57)
  def explosion = SpriteAnimation(explosionSheet, new SimpleSprite(frames = 10, delay = 100 millis), 0).get
  lazy val snarlSheet = new SpriteSheet("res/effects/snarl.png", 31, 31)
  def snarl = SpriteAnimation(snarlSheet, new SimpleSprite(frames = 8, delay = 120 millis), 0).get
  
  def placeEntitiesInLevel(player: Player, level: Level): Seq[Entity] = {
    import level.PlacedLevelCell
    
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
    /*
    val coins = level.placedCells.filter(landsEndProperty).flatMap { pc =>
      new GoldCoin(player, pc.pos * 16) ::
      new SilverCoin(player, (pc.pos + Up) * 16) ::
      new SilverCoin(player, (pc.pos + Down) * 16) ::
      new SilverCoin(player, (pc.pos + Right) * 16) ::
      new SilverCoin(player, (pc.pos + Left) * 16) :: Nil
    }
    */
    
    // Coins on every walkable cell
    val coins = level.placedCells.filter(_.cell.properties contains Walkable).map { pc =>
      new GoldCoin(player, pc.pos * 16)
    }
    coins
  }
  
  def placeChestInLevel[L <: Level](level: L, scene: Scene): Level = {
    import level.PlacedLevelCell
    
    val chestPos = Vec2d(10, 10)
    val chest = new Chest(startPos = chestPos.toVec2f * 16,
      onTouch = { t =>
        val text = new CenteredTextBox(width = 150, text = "Opened chest\nFound a potion!")
        scene.addOverlay(text)
        scene.at(t.milliseconds + 5.seconds, (_ => text.alive = false))
        Inventory.addItem(new SmallHealthPotion())
      })
    scene.addEntity(chest)
    
    val updatedCells = level.placedCells.map { _ match {
      case p if p.pos == chestPos => {
        LevelCell(layer0 = p.cell.layer0, layer1 = p.cell.layer1)(properties = (p.cell.properties.toList diff List(Walkable)): _*)
      }
      case p => p.cell
    }}
    level.copy(cells = updatedCells)
  }
}

class Chest(startPos: Vec2f, onTouch: Action) extends
    Entity(size = Vec2d(32, 32), visual = Some(Entities.chestSprite), pos = startPos) {
  visual.get.stop
  val properties = List(IsGoodRearmable)
  def tick(scene: Scene, delta: Int) = {
    if (scene.player touches this) {
      if (armed) {
        armed = false
        Audio.chestopen.play
        visual.get.setCurrentFrame(1)
        onTouch(delta)
      }
      scene.player.goBack
    } 
  }
  
  override def rearm {
    super.rearm
    visual.get.setCurrentFrame(0)
  }
}

class Collectible(player: Player, startPos: Vec2f, onTouch: Action, visual: Animation) extends 
    Entity(size = Vec2d(visual.getWidth, visual.getHeight), visual = Some(visual), pos = startPos) {
  val properties = List(IsCollectible)
  def tick(scene: Scene, delta: Int) = {
    if (player touches this) {
      alive = false
      onTouch(delta)
    }
  }
}

class Effect(startPos: Vec2f, visual: Animation) extends
    Entity(size = Vec2d(visual.getWidth, visual.getHeight), visual = Some(visual), pos = startPos) {
  val properties = Nil
  visual.setLooping(false)
  def tick(scene: Scene, delta: Int) = {
    if (visual.getFrame == visual.getFrameCount - 1) {
      alive = false
    }
  } 
}

class GoldCoin(player: Player, startPos: Vec2d) extends
  Collectible(player = player, startPos = startPos.toVec2f, visual = Entities.goldCoinSprite, onTouch = { () => 
    Audio.pling.play
    player.gold += 10
})

class SilverCoin(player: Player, startPos: Vec2d) extends
  Collectible(player = player, startPos = startPos.toVec2f, visual = Entities.silverCoinSprite, onTouch = { () =>
    Audio.pling.play
    player.gold += 5
})

class CopperCoin(player: Player, startPos: Vec2d) extends
  Collectible(player = player, startPos = startPos.toVec2f, visual = Entities.copperCoinSprite, onTouch = { () =>
    Audio.pling.play
    player.gold += 1
})

class Explosion(startPos: Vec2f) extends Effect(startPos, Entities.explosion)

class CenteredTextBox(width: Int, text: String) extends TextBox(width, text, Vec2d(200 - width / 2, 12))

class FemaleOrc(level: Level, player: Player, brain: Brain, startPos: Vec2d) extends Humanoid (
    level = level,
    player = player,
    skin = Entities.femaleOrcSkin,
    defaultBehavior = brain,
    spriteAction = Walk,
    startPosition = startPos.toVec2f,
    speed = 0.3f,
    maxHp = 100,
    damage = 3,
    onHurt = Audio.grunt.play _,
    onDie = Audio.growl.play _,
    onBeginAttack = Audio.slash.loop _,
    onEndAttack = Audio.slash.stop _
)

