package de.atextor.madv.engine

import scala.language.postfixOps
import swing._
import event._
import scala.util.Random
import Util._
import java.awt.BasicStroke
import CellularAutomaton.Rule

object Main extends SimpleSwingApplication {
  import event.Key._
  import java.awt.{Dimension, Graphics2D, Graphics, Image, Rectangle}
  import java.awt.{Color => AWTColor}
  import java.awt.event.{ActionEvent}
  import javax.swing.{Timer => SwingTimer, AbstractAction}
  
  import CellularAutomaton.Area
  
  val caveRule = new Rule(born = Set(6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
  val smoothRule = new Rule(born = Set(5, 6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
  var grid = CellularAutomaton(50, 50, Set()).randomFill(0.5)
  var scale = 5
  
  override def top: Frame = frame
  val frame = new MainFrame {
    title = "GOL"
    contents = new FlowPanel {
      background = AWTColor.white
      preferredSize = new Dimension(800, 700)
      val factor = new TextField("0.5")
      val cavesize = new TextField("50")
      val cave = new Button("Cave")
      val smooth = new Button("Smooth")
      val upscale = new Button("Upscale")
      val reset = new Button("Reset")
      val fill = new Button("Fill")
      val neigh = new Button("Neighbors")
      val fixpotholes = new Button("Fix Potholes")
      
      contents.append(mainPanel, factor, cavesize, cave, smooth, upscale, reset, fill, neigh, fixpotholes)
      listenTo(cave, smooth, upscale, reset, fill, neigh, fixpotholes)
      reactions += {
        case ButtonClicked(`cave`) =>
          areas = Set()
          grid = grid(caveRule)
          repaint()
        case ButtonClicked(`smooth`) =>
          areas = Set()
          grid = grid(smoothRule)
          repaint()
        case ButtonClicked(`upscale`) =>
          scale -= 1
          areas = Set()
          if (scale == 0) scale = 1
          grid = grid.upscale
          repaint()
        case ButtonClicked(`reset`) =>
          neighbors = Set()
          scale = 5
          areas = Set()
          grid = CellularAutomaton(width = cavesize.text.toInt, height = cavesize.text.toInt,
              liveCells = Set()).randomFill(factor.text.toDouble)
          repaint()
        case ButtonClicked(`fill`) =>
          areas = grid.areas
          repaint()
        case ButtonClicked(`neigh`) =>
          neighbors = grid.areas |> (grid.sortAreasBySize(_)) |> (grid.findNeighborAreas(_))
          repaint()
        case ButtonClicked(`fixpotholes`) =>
          grid = Level.fixPotholes(grid)
          repaint()
      }
    }
    
    var areas: Set[Area] = Set()
    var neighbors: Set[(Area, Area)]= Set()
    val stroke = new BasicStroke(3F)
    
    lazy val mainPanel = new Panel() {
      focusable = true
      background = AWTColor.white
      preferredSize = new Dimension(800, 600)
      
      def cellToRect(c: Cell) = new Rectangle(c.x * scale, c.y * scale, scale, scale);
      
      def drawGrid(grid: CellularAutomaton, g: Graphics2D) {
        val darkRed = new AWTColor(200, 100, 100)
        g.setColor(AWTColor.gray)
        grid.allCells.foreach(c => g.draw(cellToRect(c)))
        g.setColor(darkRed)
        if (areas.isEmpty) {
          grid.allCells.filter(grid.isAlive).foreach(c => g.fill(cellToRect(c))) 
        } else {
          for (a <- areas) {
            val col = new AWTColor(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))
            g.setColor(col)
            a.foreach(c => g.fill(cellToRect(c)))
          }
        }
        
        if (!neighbors.isEmpty) {
          g.setStroke(stroke)
          neighbors.foreach{case (a, b) =>
            val aCenter = a.foldLeft(Cell(0, 0))(_ + _) |> (c => Cell(c.x / a.size, c.y / a.size))
            val bCenter = b.foldLeft(Cell(0, 0))(_ + _) |> (c => Cell(c.x / b.size, c.y / b.size))
            g.drawLine(aCenter.x * scale, aCenter.y * scale, bCenter.x * scale, bCenter.y * scale)
          }
        }
      }

      override def paint(g: Graphics2D) {
        g.setColor(AWTColor.white)
        g.fillRect(0, 0, size.width, size.height)
        
        drawGrid(grid, g)
      }
    }
    
  }
}