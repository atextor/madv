package de.atextor.madv.game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.state.BasicGameState
import org.newdawn.slick.state.StateBasedGame

import de.atextor.madv.engine.Down
import de.atextor.madv.engine.EntitySkin
import de.atextor.madv.engine.Hurt
import de.atextor.madv.engine.Slash
import de.atextor.madv.engine.Spellcast
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walk

class TitleScreen extends BasicGameState {
  override val getID = 1
  
  var test: EntitySkin = null
  def init(gc: GameContainer, game: StateBasedGame) {
  import de.atextor.madv.engine.PartName._
  val test = EntitySkin(Vec2d(64, 64), List(Hurt, Slash, Spellcast, Walk),
     (body -> ("female" :: Nil)),
     (head -> ("female_darkblondehair" :: Nil)),
     (torso -> ("female_vest" :: "female_forestrobe" :: Nil)),
     (belt -> ("female_blackbelt" :: "female_ironbuckle" :: Nil)),
     (feet -> ("female_grayslippers" :: Nil)))
  }
  
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) {
    g.scale(2, 2)
    test.draw(Down, Walk, Vec2d(160, 50))
  }
  
  def update(gc: GameContainer, game: StateBasedGame, delta: Int) {
  }
}