package de.atextor.madv.game

import org.newdawn.slick.AppGameContainer
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Input
import org.newdawn.slick.state.StateBasedGame
import org.newdawn.slick.SpriteSheet
import de.atextor.madv.engine.Text
import org.newdawn.slick.state.transition.CrossStateTransition
import org.newdawn.slick.state.GameState
import org.newdawn.slick.state.transition.EmptyTransition
import de.atextor.madv.engine.Audio

class Madv extends StateBasedGame("Madv") {
  var container: AppGameContainer = null
  
  def toggleFullscreen {
    if (container == null) return
    container.setDisplayMode(800, 600, !container.isFullscreen)
  }
  
  def initStatesList(gc: GameContainer) {
    val target = new LevelScene(toggleFullscreen _)
    addState(new TitleScreen(toggleFullscreen _, startGame = { () =>
      Audio.music1.stop
      Audio.music2.loop
      val transition = new CrossStateTransition(target) {
        def isComplete = true
        def init(firstState: GameState, secondState: GameState) {}
      }
      enterState(2, transition, new EmptyTransition());
    }))
    addState(target)
  }
}

object Madv {
  def main(args: Array[String]) {
    Input.disableControllers();
    val madv = new Madv
    val container = new AppGameContainer(madv)
    madv.container = container 
    container.setShowFPS(false)
    container.setDisplayMode(800, 600, true)
    container.setTargetFrameRate(60)
    container.start
  }
}
