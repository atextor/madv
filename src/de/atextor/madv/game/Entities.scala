package de.atextor.madv.game

import de.atextor.madv.engine.Belt
import de.atextor.madv.engine.Body
import de.atextor.madv.engine.Down
import de.atextor.madv.engine.EntitySkin
import de.atextor.madv.engine.Feet
import de.atextor.madv.engine.Head
import de.atextor.madv.engine.Humanoid
import de.atextor.madv.engine.Hurt
import de.atextor.madv.engine.Slash
import de.atextor.madv.engine.Spellcast
import de.atextor.madv.engine.Torso
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walk

import de.atextor.madv.engine.Util.pipelineSyntax

object Entities {
  val playerSkin = EntitySkin(Vec2d(64, 64), List(Hurt, Slash, Spellcast, Walk),
    body = List(Body("female")),
    head = List(Head("female_darkblondehair")),
    torso = List(Torso("female_vest"), Torso("female_forestrobe")),
    belt = List(Belt("female_blackbelt"), Belt("female_ironbuckle")),
    feet = List(Feet("female_grayslippers")))
}