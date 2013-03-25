package de.atextor.madv.game

import de.atextor.madv.engine.GameItem
import de.atextor.madv.engine.MagicMapping
import de.atextor.madv.engine.PlayerHealth

case class Potion() extends GameItem("Potion", "A strange potion.\nWill restore 100 HP.", Some(PlayerHealth))

case class MagicMapScroll() extends GameItem("Magic Map Scroll",
    "This scroll will uncover\nthe whole map.", Some(MagicMapping))
