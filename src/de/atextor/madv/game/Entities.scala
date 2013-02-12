package de.atextor.madv.game

import de.atextor.madv.engine.EntitySkin
import de.atextor.madv.engine.Hurt
import de.atextor.madv.engine.Slash
import de.atextor.madv.engine.Spellcast
import de.atextor.madv.engine.Util.pipelineSyntax
import de.atextor.madv.engine.Vec2d
import de.atextor.madv.engine.Walk
import de.atextor.madv.engine.PartName._

object Entities {
  lazy val playerSkin = EntitySkin(Vec2d(64, 64), List(Hurt, Slash, Spellcast, Walk),
     (body ->  ("female" :: Nil)),
     (head ->  ("female_darkblondehair" :: Nil)),
     (torso -> ("female_vest" :: "female_forestrobe" :: Nil)),
     (belt ->  ("female_blackbelt" :: "female_ironbuckle" :: Nil)),
     (feet ->  ("female_grayslippers" :: Nil)))
}