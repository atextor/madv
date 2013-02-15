package de.atextor.madv.engine

import scala.language.reflectiveCalls
import scala.language.postfixOps
import scala.concurrent.duration._
import de.atextor.madv.engine.Util._
import org.newdawn.slick.SpriteSheet
import org.newdawn.slick.Animation

sealed abstract class SpriteAction(
  val frames: Int,
  val delay: Duration,
  val spriteRow: (Direction => Int) = _.id)

case class SimpleSprite(override val frames: Int, override val delay: Duration) extends SpriteAction(frames, delay)
case object Bow extends SpriteAction(13, 100 millis)
case object Hurt extends SpriteAction(6, 150 millis, (_ => 0))
case object Slash extends SpriteAction(6, 100 millis)
case object Spellcast extends SpriteAction(7, 100 millis)
case object Thrust extends SpriteAction(8, 100 millis)
case object Walk extends SpriteAction(8, 100 millis)

object SpriteAnimation extends ((SpriteSheet, SpriteAction, Int) => Animation) {
  def apply(sheet: SpriteSheet, sa: SpriteAction, row: Int): Animation = {
    val ani = new Animation
    for (x <- 0 until sa.frames) {
      ani.addFrame(sheet.getSprite(x, row), sa.delay.toMillis.toInt)
    }
    ani setPingPong false
    ani
  }
}

case class Part(name: String) {
  private def animation(d: Direction, a: SpriteAction): Animation =
    SpriteAnimation(new SpriteSheet(s"res/sprites/${a.toString.toLowerCase}/${name}.png", 64, 64), a, a.spriteRow(d))
  val getAnimation = Memoize.memoize(animation _)
  def draw(d: Direction, a: SpriteAction, position: Vec) = getAnimation(d, a).draw(position.x, position.y)
  def stopAnimation(d: Direction, a: SpriteAction) = getAnimation(d, a).stop
  def startAnimation(d: Direction, a: SpriteAction) = getAnimation(d, a).start
}

object PartName extends Enumeration {
  val behind, body, feet, legs, torso, belt, head, hands, weapon = Value
}

case class EntitySkin(val size: Vec2d, actions: List[SpriteAction], parts: (PartName.Value, Seq[String])*) {
  import PartName._
  val m = parts.map{case (partName, images) => (partName, images.map(i => Part(partName.toString + "_" + i)))}.toMap
  def draw(d: Direction, a: SpriteAction, position: Vec) {
    m get behind foreach(_.foreach(_.draw(d, a, position)))
    m get body foreach(_.foreach(_.draw(d, a, position)))
    m get feet foreach(_.foreach(_.draw(d, a, position)))
    m get legs foreach(_.foreach(_.draw(d, a, position)))
    m get torso foreach(_.foreach(_.draw(d, a, position)))
    m get belt foreach(_.foreach(_.draw(d, a, position)))
    m get head foreach(_.foreach(_.draw(d, a, position)))
    m get hands foreach(_.foreach(_.draw(d, a, position)))
    m get weapon foreach(_.foreach(_.draw(d, a, position)))
  }
  
  def stopAnimation(d: Direction, a: SpriteAction) = m.values.foreach(_.foreach(_.stopAnimation(d, a)))
  def startAnimation(d: Direction, a: SpriteAction) = m.values.foreach(_.foreach(_.startAnimation(d, a)))
}
