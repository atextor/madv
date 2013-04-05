package de.atextor.madv.engine

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import org.newdawn.slick.Animation
import org.newdawn.slick.Renderable
import org.newdawn.slick.SpriteSheet
import org.newdawn.slick.Sound

trait Tickable {
  var enabled = true
  final def update(scene: Scene, delta: Int) = if (enabled) tick(scene, delta)
  def tick(scene: Scene, delta: Int)
}

trait CanGo {
  def go(d: Direction)
}

trait EntityProperty
case object IsMonster extends EntityProperty
case object IsGoodRearmable extends EntityProperty
case object IsCollectible extends EntityProperty
case object IsTarget extends EntityProperty

abstract class Entity(var size: Vec2d, val visual: Option[Animation] = None, override var pos: Vec2f) extends Movable with Tickable with Renderable {
  var lookingDirection: Direction = Down
  var movingDirection: Vec2f = Nowhere
  var alive = true
  var armed = true
  def draw(x: Float, y: Float) = visual.foreach(_.draw(x, y))
  def hurt(damage: Int) {}
  def properties: List[EntityProperty]
  def rearm = armed = true
  
  def relativeDraw(base: Vec[Float], staticOffset: Vec[Int]) {
    if (enabled) {
      draw((pos.x - base.x) * 2 + staticOffset.x + 8, (pos.y - base.y) * 2 + staticOffset.y + 32)
    }
  }
}

class Humanoid (
    level: Level,
    player: Player,
    val skin: EntitySkin,
    val defaultBehavior: Brain = Dumb,
    var spriteAction: SpriteAction = Walk,
    startPosition: Vec2f,
    var speed: Float,
    var maxHp: Int,
    val damage: Int,
    val hurtSound: Option[Sound],
    val dieSound: Option[Sound],
    val attackSound: Option[Sound]) extends Entity(size = skin.size, pos = startPosition) with CanGo {
  
  var hp = maxHp
  var properties: List[EntityProperty] = List(IsMonster, IsTarget)
  val shadow = UI.image("res/sprites/humanoid_shadow.png")
  var behavior = defaultBehavior
  
  def tick(scene: Scene, delta: Int) = behavior(this, scene, delta)
  
  override def draw(x: Float, y: Float) = {
    if (spriteAction != Hurt) shadow.draw(x - 8, y - 26)
    skin.draw(lookingDirection, spriteAction, Vec2d(x.toInt - 8, y.toInt - 31)) 
  }
  
  def stop = {
    movingDirection = Nowhere
    spriteAction = Walk
    skin.stopAnimation(lookingDirection, spriteAction)
  }
  
  def go(d: Direction) {
    spriteAction = Walk
    movingDirection = movingDirection(d)
    lookingDirection = d
    skin.startAnimation(lookingDirection, spriteAction)
  }
  
  def goForward = pos += movingDirection * speed
  def goBack = pos += movingDirection.invert * speed 
  
  def canMove = {
    val dest = pos + movingDirection * speed
    level.cellAt(dest.toVec2d).properties contains Walkable
  }
  
  def chase {
    behavior = defaultBehavior
    spriteAction = Walk
  }
  
  def die {
    attackSound.foreach(_.stop())
    dieSound.foreach(_.play())
    properties = properties.filter(_ != IsTarget)
    movingDirection = movingDirection(Down)
    lookingDirection = Down
    spriteAction = Hurt
    behavior = Dying
    skin.startAnimation(Down, Hurt)
  }
  
  def attack {
    attackSound.foreach(_.loop())
    movingDirection = Nowhere
    spriteAction = Slash
    behavior = new Attack(player, damage)
  }
  
  override def move = {
    if (movingDirection != Nowhere) {
      if (canMove) goForward
    }
  }
  
  override def hurt(damage: Int) {
    if (spriteAction == Hurt) return
    hurtSound.foreach(_.play())
    hp -= damage
    if (hp <= 0) die
  }
}
