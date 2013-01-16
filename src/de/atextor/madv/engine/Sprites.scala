package de.atextor.madv.engine

import scala.language.reflectiveCalls
import scala.language.postfixOps
import de.atextor.madv.engine.Util._
import org.newdawn.slick.SpriteSheet
import org.newdawn.slick.Animation

sealed abstract class Action(
  val frames: Int,
  val delay: Duration,
  val spriteRow: (Direction => Int) = _.id)
case object Bow extends Action(13, 100 ms)
case object Hurt extends Action(6, 150 ms, (_ => 0))
case object Slash extends Action(6, 100 ms)
case object Spellcast extends Action(7, 100 ms)
case object Thrust extends Action(8, 100 ms)
case object Walk extends Action(9, 120 ms)

sealed abstract class Part(name: String) {
  private def animation(d: Direction, a: Action): Animation = {
    val ss = new SpriteSheet(s"res/sprites/${a.toString.toLowerCase}/${name}.png", 64, 64); 
    val ani = new Animation
    for (x <- 0 until a.frames) {
      ani.addFrame(ss.getSprite(x, a.spriteRow(d)), a.delay.toMillis.toInt)
    }
    ani
  }
    
  import Memoize._
  val getAnimation = memoize(animation _)
    
  def draw(d: Direction, a: Action, position: Vec) = getAnimation(d, a).draw(position.x, position.y)
}
case class Weapon(name: String) extends Part("weapon_" + name)
case class Hands(name: String) extends Part("hands_" + name)
case class Head(name: String) extends Part("head_" + name)
case class Belt(name: String) extends Part("belt_" + name)
case class Torso(name: String) extends Part("torso_" + name)
case class Legs(name: String) extends Part("legs_" + name)
case class Feet(name: String) extends Part("feet_" + name)
case class Body(name: String) extends Part("body_" + name)
case class Behind(name: String) extends Part("behind_" + name)

case class EntitySkin(
    actions: List[Action],
    weapon: List[Weapon] = Nil,
    hands: List[Hands] = Nil,
    head: List[Head] = Nil,
    belt: List[Belt] = Nil,
    torso: List[Torso] = Nil,
    legs: List[Legs] = Nil,
    feet: List[Feet] = Nil,
    body: List[Body] = Nil,
    behind: List[Behind] = Nil) {
  def draw(d: Direction, a: Action, position: Vec) {
    // optimal drawing order taken from artists readme :)
    behind foreach (_.draw(d, a, position))
    body foreach (_.draw(d, a, position))
    feet foreach (_.draw(d, a, position))
    legs foreach (_.draw(d, a, position))
    torso foreach (_.draw(d, a, position))
    belt foreach (_.draw(d, a, position))
    head foreach (_.draw(d, a, position))
    hands foreach (_.draw(d, a, position))
    weapon foreach (_.draw(d, a, position))
  }
}
  