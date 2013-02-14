package de.atextor.madv.game

import scala.concurrent.duration._
import org.newdawn.slick.SpriteSheet
import de.atextor.madv.engine.Entity
import de.atextor.madv.engine.EntitySkin
import de.atextor.madv.engine.Hurt
import de.atextor.madv.engine.PartName.belt
import de.atextor.madv.engine.PartName.body
import de.atextor.madv.engine.PartName.feet
import de.atextor.madv.engine.PartName.head
import de.atextor.madv.engine.PartName.torso
import de.atextor.madv.engine.Slash
import de.atextor.madv.engine.Spellcast
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walk
import de.atextor.madv.engine.Action
import de.atextor.madv.engine.SpriteAction
import de.atextor.madv.engine.SimpleSprite
import de.atextor.madv.engine.SpriteAnimation

object Entities {
  private def sprite(sheet: String, size: Int, frames: Int, delay: Duration) =
    SpriteAnimation(new SpriteSheet(sheet, size, size), new SimpleSprite(frames, delay), 0)
  lazy val playerSkin = EntitySkin(Vec2d(64, 64), List(Hurt, Slash, Spellcast, Walk),
     (body  -> ("female" :: Nil)),
     (head  -> ("female_darkblondehair" :: Nil)),
     (torso -> ("female_vest" :: "female_forestrobe" :: Nil)),
     (belt  -> ("female_blackbelt" :: "female_ironbuckle" :: Nil)),
     (feet  -> ("female_grayslippers" :: Nil)))
  lazy val goldCoinSprite = sprite(sheet = "res/items/coin_gold.png", size = 32, frames = 8, delay = 60 millis)
}

class Coin(player: Player, startPos: Vec2d, onTouch: Action) extends
    Entity(size = Vec2d(32, 32), visual = Some(Entities.goldCoinSprite), pos = startPos) {
  def tick(delta: Int) = {
    if (player touches this) {
      alive = false
      onTouch(delta)
    }
  }
}
