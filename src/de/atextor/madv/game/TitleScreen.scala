package de.atextor.madv.game

import org.newdawn.slick.state.BasicGameState
import org.newdawn.slick.GameContainer
import org.newdawn.slick.state.StateBasedGame
import org.newdawn.slick.Graphics
import de.atextor.madv.engine.EntitySkin
import de.atextor.madv.engine.Hurt
import de.atextor.madv.engine.Slash
import de.atextor.madv.engine.Spellcast
import de.atextor.madv.engine.Walk
import de.atextor.madv.engine.Head
import de.atextor.madv.engine.Belt
import de.atextor.madv.engine.Torso
import de.atextor.madv.engine.Feet
import de.atextor.madv.engine.Body
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.Vec2d
import org.newdawn.slick.Input

class TitleScreen extends BasicGameState {
  override val getID = 1
  
  var test: EntitySkin = null
  def init(gc: GameContainer, game: StateBasedGame) {
    test = EntitySkin(List(Hurt, Slash, Spellcast, Walk),
        body = List(Body("female")),
        head = List(Head("female_darkblondehair")),
        torso = List(Torso("female_vest"), Torso("female_forestrobe")),
        belt = List(Belt("female_blackbelt"), Belt("female_ironbuckle")),
        feet = List(Feet("female_grayslippers")))
  }
  
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) {
    g.scale(2, 2)
    test.draw(Down, Walk, Vec2d(160, 50))
  }
  
  def update(gc: GameContainer, game: StateBasedGame, delta: Int) {
  }
}