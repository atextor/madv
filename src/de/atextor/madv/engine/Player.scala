package de.atextor.madv.engine

import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.Duration
import org.newdawn.slick.Animation

class Player(level: Level, startPosition: Vec2d, entitySkin: EntitySkin) extends Humanoid(
    player = null,
    level = level,
    skin = entitySkin,
    spriteAction = Walk,
    startPosition = startPosition.toVec2f,
    speed = (if (Constants.debug) 5 else 1),
    hp = 100,
    damage = 0
) {
  val staticRenderPos = Vec2d(168, 80)
  var armed = true
  var spell: Option[Spell] = None
  
  override def draw(x: Float, y: Float) = {
    shadow.draw(x, y + 5)
    skin.draw(lookingDirection, spriteAction, Vec2d(x.toInt, y.toInt)) 
  }
  
  override def touchTopLeft = pos + Vec2f(27, 25)
  override def touchBottomRight = pos + Vec2f(15, 18)
  
  override def move {
    pos += movingDirection * speed
    if (Constants.debug) return
//    if (!(level.cellAt(pos.toVec2d).properties contains Walkable)) goBack
  }
  
  override def hurt(damage: Int) {
    super.hurt(damage)
    if (hp <= 0) {
      alive = false
    }
  }
  
  override def tick(scene: Scene, delta: Int) {
    if (spriteAction == Spellcast && armed) {
      val s = spell.get
      addEntities(s(pos))
      armed = false
      at((delta millis) + s.cooldown, {_ => armed = true})
    }
  }
  
  override def attack {
    spell.foreach { s =>
      movingDirection = Nowhere
      spriteAction = Spellcast
      skin.startAnimation(lookingDirection, spriteAction)
    }
  }
}

abstract class Spell(val cooldown: Duration) extends (Vec2f => Seq[Entity])

class Shooter(shoot: Vec2f => Projectile, cooldown: Duration) extends Spell(cooldown) {
  def apply(pos: Vec2f) = {
    shoot(pos) :: Nil
  }
}

class Projectile(spawner: Entity, visual: Animation, speed: Float) extends
    Entity(size = OnePixelSize, visual = Some(visual), pos = spawner.pos + Vec2d(4, 0)) {
  val isTarget = false
  movingDirection = spawner.lookingDirection * speed
  
  override def touches(other: Movable): Boolean = distanceTo(other) < 3
  
  def tick(scene: Scene, delta: Int) {
    if (distanceTo(spawner) > 100) {
      alive = false
    }
    
    scene.entities.filter(_.isTarget).foreach {e =>
      if (e touches this) {
        alive = false
        return
      }
    }
  }
}
