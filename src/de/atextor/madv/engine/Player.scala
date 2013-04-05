package de.atextor.madv.engine

import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import org.newdawn.slick.Animation
import de.atextor.madv.game.Effect

class Player(var level: Level, startPosition: Vec2d, entitySkin: EntitySkin, nextLevel: () => Unit) extends Humanoid(
    player = null,
    level = level,
    skin = entitySkin,
    spriteAction = Walk,
    startPosition = startPosition.toVec2f,
    speed = 1,
    maxHp = 100,
    damage = 0,
    hurtSound = None,
    dieSound = None,
    attackSound = None
) {
  val staticRenderPos = Vec2d(168, 80)
  var spell: Option[Spell] = None
  var cooldownBoost = 0
  var armor = 0
  var autoMap: Option[AutoMap] = None
  var gold: Int = 0
  
  override def draw(x: Float, y: Float) = {
    shadow.draw(x, y + 5)
    skin.draw(lookingDirection, spriteAction, Vec2d(x.toInt, y.toInt)) 
  }
  
  override def touchTopLeft = pos + Vec2f(27, 25)
  override def touchBottomRight = pos + Vec2f(15, 18)
  
  override def move {
    pos += movingDirection * speed
    val p = level.cellAt(pos.toVec2d).properties
    if (p contains Exit) nextLevel()
    if (Constants.debug) return
    if (!(p contains Walkable)) goBack
  }
  
  override def hurt(damage: Int) {
    super.hurt(if (damage < 0) damage else Math.abs(damage - armor))
    if (hp <= 0) {
      alive = false
    }
  }
  
  override def tick(scene: Scene, delta: Int) {
    if (spriteAction == Spellcast && armed) {
      val s = spell.get
      armed = false
      scene.addEntities(s(pos))
      scene.at((delta millis) + s.cooldown - (s.cooldown * cooldownBoost / 100), {_ => armed = true})
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

abstract class Spell(val cooldown: Duration, onFire: () => Unit = DoNothing) extends (Vec2f => Seq[Entity])

class Shooter(shoot: Vec2f => Projectile, cooldown: Duration, onFire: () => Unit = DoNothing) extends Spell(cooldown, onFire) {
  def apply(pos: Vec2f) = {
    onFire()
    shoot(pos) :: Nil
  }
}

class Projectile(spawner: Entity, visual: Animation, speed: Float, damage: Int, directional: Boolean = false, onHit: Vec2f => Seq[Effect] = {Nowhere => Nil}) extends
    Entity(size = OnePixelSize, visual = Some(visual), pos = spawner.pos + Vec2d(4, 0)) {
  val properties = Nil
  movingDirection = spawner.lookingDirection * speed
  
  if (directional) {
    visual.stop
    spawner.lookingDirection match {
      case Up => visual.setCurrentFrame(0)
      case Right => visual.setCurrentFrame(2)
      case Down => visual.setCurrentFrame(4)
      case Left => visual.setCurrentFrame(6)
    }
  }
  
  def tick(scene: Scene, delta: Int) {
    if (distanceTo(spawner) > 100) {
      alive = false
    }
    
    scene.entities.filter(_.properties contains IsTarget).foreach {e =>
      val dist = distanceTo(e)
      if (dist < 7) {
        e.hurt(damage)
        scene.addEffects(onHit(pos))
        alive = false
        return
      }
    }
  }
}
