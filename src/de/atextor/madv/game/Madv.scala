package de.atextor.madv.game

import org.newdawn.slick.AppGameContainer
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Input
import org.newdawn.slick.state.StateBasedGame

class Madv extends StateBasedGame("Madv") {
  var container: GameContainer = null
  
  def initStatesList(gc: GameContainer) {
    container = gc
//    addState(new TitleScreen)
    addState(new LevelTest)
  }
  
  override def keyPressed(key: Int, c: Char) {
    super.keyPressed(key, c)
    if (key == Input.KEY_ESCAPE) {
      container.exit
    }
  }
}

object Madv {
  def main(args: Array[String]) {
    val container = new AppGameContainer(new Madv)
    container.setDisplayMode(800, 600, false)
    container.setTargetFrameRate(60)
    container.start
  }
}
