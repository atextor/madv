package de.atextor.madv.engine

import org.newdawn.slick.openal.AudioLoader
import org.newdawn.slick.util.ResourceLoader
import org.newdawn.slick.Sound
import scala.util.Random
import de.atextor.madv.game.Madv
import org.newdawn.slick.Music

object Audio {
  private def sound(name: String) = new Sound(s"res/audio/${name}.wav")
  lazy val pling = sound("pling")
  lazy val chestopen = sound("dooropen")
  lazy val grunt = sound("doggrunt")
  lazy val growl = sound("growl")
  lazy val slash = sound("slash")
  lazy val success = sound("success")
  lazy val teleport = sound("teleport")
  lazy val cashregister = sound("cashregister")
  lazy val explosion = sound("explosion")
  lazy val bad = sound("bad")
  lazy val bonecrack = sound("bonecrack")
  lazy val bonecrack2 = sound("bonecrack2")
  
  private lazy val shoots = (1 to 5).toList.map(n => sound(s"shoot${n}"))
  private def randomShootSound = Random shuffle shoots head
  def shoot = randomShootSound.play
  
  private def music(name: String) = new Music(s"res/audio/${name}.ogg", true);
  lazy val music1 = music("03-theme")
  lazy val music2 = music("04-caves")
  lazy val music3 = music("01-caves2")
  lazy val music4 = music("02-caves3")
}