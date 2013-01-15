package de.atextor.madv.game

import org.newdawn.slick.Animation
import org.newdawn.slick.AppGameContainer
import org.newdawn.slick.BasicGame
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Input

import de.atextor.madv.engine._

class Madv extends BasicGame("Madv") {
  var container: GameContainer = null
  var ani: Animation = null
  var ani2: Animation = null
  
  def update(container: GameContainer, delta: Int) {
  }
  
//  var player: Entity = null
  var test: EntitySkin = null
  
  def init(container: GameContainer) {
    this.container = container
//    test = EntitySkin(List(Hurt, Slash, Spellcast, Walk),
//        body = List(Body("skeleton")),
//        head = List(Head("chain_armor_hood")),
//        torso = List(Torso("plate_armor_arms_shoulders")))
    test = EntitySkin(List(Hurt, Slash, Spellcast, Walk),
        body = List(Body("female")),
        head = List(Head("female_darkblondehair")),
        torso = List(Torso("female_vest"), Torso("female_forestrobe")),
        belt = List(Belt("female_blackbelt"), Belt("female_ironbuckle")),
        feet = List(Feet("female_grayslippers")))
  }
  
  def render(container: GameContainer, g: Graphics) {
    g.scale(2, 2)
    test.draw(Down, Walk, 160, 50)
  }
  
  override def keyPressed(key: Int, c: Char) {
    if (key == Input.KEY_ESCAPE) {
      container.exit
    }
  }
}

object Madv extends App {
  val container = new AppGameContainer(new Madv)
  container.setDisplayMode(800, 600, false)
  container.start

}