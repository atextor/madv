package de.atextor.madv.engine

import org.newdawn.slick.openal.AudioLoader
import org.newdawn.slick.util.ResourceLoader
import org.newdawn.slick.Sound

object Audio {
  lazy val pling = new Sound("res/audio/pling.wav")
  lazy val chestopen = new Sound("res/audio/dooropen.wav")
  lazy val grunt = new Sound("res/audio/doggrunt.wav")
  lazy val growl = new Sound("res/audio/growl.wav")
  
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