package de.atextor.madv.game

import scala.util.Random
import org.newdawn.slick.Music
import org.newdawn.slick.Sound

object Audio {
  private def sound(name: String) = new Sound(s"res/audio/${name}.wav")
  def pling = sound("pling")
  def chestopen = sound("dooropen")
  def grunt = sound("doggrunt")
  def growl = sound("growl")
  def slash = sound("slash")
  def success = sound("success")
  def teleport = sound("teleport")
  def cashregister = sound("cashregister")
  def explosion = sound("explosion")
  def bad = sound("bad")
  def bonecrack = sound("bonecrack")
  def bonecrack2 = sound("bonecrack2")
  
  private lazy val shoots = (1 to 5).toList.map(n => sound(s"shoot${n}"))
  private def randomShootSound = Random shuffle shoots head
  def shoot = randomShootSound.play
  
  private def music(name: String) = new Music(s"res/audio/${name}.ogg", true);
  lazy val music1 = music("03-theme")
  lazy val music2 = music("04-caves")
  lazy val music3 = music("01-caves2")
  lazy val music4 = music("02-caves3")
}