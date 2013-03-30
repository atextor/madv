package de.atextor.madv.engine

import org.newdawn.slick.openal.AudioLoader
import org.newdawn.slick.util.ResourceLoader
import org.newdawn.slick.Sound
import scala.util.Random

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
  
  private lazy val shoots = (1 to 5).toList.map(n => sound(s"shoot${n}"))
  private def randomShootSound = Random shuffle shoots head
  def shoot = randomShootSound.play
  
//  val stream = classOf[Audio].getClassLoader.getResourceAsStream("sample.ogg")
  
//  val ogg = new OggClip(stream);
//  ogg.setBalance(0f);
//  ogg.setGain(1.0f);
//  ogg.loop
//  ogg.pause
//  ogg.resume
//  ogg.stop
//  ogg.setGain(1.0f) 
  
}