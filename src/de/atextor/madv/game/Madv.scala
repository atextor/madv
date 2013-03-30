package de.atextor.madv.game

import org.newdawn.slick.AppGameContainer
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Input
import org.newdawn.slick.state.StateBasedGame
import org.newdawn.slick.SpriteSheet
import de.atextor.madv.engine.Text

class Madv extends StateBasedGame("Madv") {
  var container: AppGameContainer = null
  
  def toggleFullscreen {
    if (container == null) return
    container.setDisplayMode(800, 600, !container.isFullscreen)
  }
  
  def initStatesList(gc: GameContainer) {
//    container = gc
//    addState(new TitleScreen)
    addState(new LevelTest(toggleFullscreen _))
  }
}

object Madv {
  def main(args: Array[String]) {
    Input.disableControllers();
    val madv = new Madv
    val container = new AppGameContainer(madv)
    madv.container = container 
    container.setShowFPS(false)
    container.setDisplayMode(800, 600, false)
    container.setTargetFrameRate(60)
    container.start
  }
}
