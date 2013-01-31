package de.atextor.madv.engine

import scala.language.postfixOps

/*
 * This module implements a cellular automaton for level generation, as described in
 * http://jeremykun.com/2012/07/29/the-cellular-automaton-method-for-cave-generation/
 * 
 * Furthermore, it includes algorithms to separate single caves (using recursive flood fill)
 * and detection of adjacent caves.
 */

import scala.language.reflectiveCalls
import scala.util.Random
import Util.pipelineSyntax

import CellularAutomaton.Area
import CellularAutomaton.Rule

object CellularAutomaton {
  // A rule that evolves the CA by one tick. If x E born cells around a dead cell are alive, the
  // dead cell is born. If y E survive cells around a live cell are alive, the cell stays alive.
  class Rule(val born: Set[Int], val survive: Set[Int])
  
  // An Area is a cohesive set of cells, i.e., the cells are contiguous.
  // This constraint is not enforced programmatically.
  type Area = Set[Cell]
}

case class Cell(override val x: Int, override val y: Int) extends Vec(x, y) {
  def neighbors = (for (ox <- -1 to 1; oy <- -1 to 1)
    yield Cell(x + ox, y + oy)).toList.filterNot(_ == this)
  def +[T <: Vec](c: T) = Cell(x + c.x, y + c.y)
}

case class CellularAutomaton(val width: Int, val height: Int, val liveCells: Set[Cell] = Set()) {
  lazy val allCells = (for (y <- 0 until height; x <- 0 until width) yield Cell(x, y))
  def isAlive(c: Cell) = liveCells contains c
  def inGrid(c: Cell) = c.x >= 0 && c.y >= 0 && c.x < width && c.y < height
  def isDead(c: Cell) = !isAlive(c)
  def neighborCount(c: Cell) = c.neighbors filter inGrid map isAlive filter identity size
  def randomCell = Cell(Random.nextInt(width - 1), Random.nextInt(height - 1))
  def randomDeadCell = Stream continually randomCell find isDead get
  def randomFill(density: Double) = copy(liveCells =
    Stream.continually(randomDeadCell).take((width * height * density).toInt).toSet)
  def addLiveBorder = copy(liveCells = (liveCells ++
    (0 until height).map(Cell(0, _)).toSet ++
    (0 until height).map(Cell(width - 1, _)).toSet ++
    (0 until width).map(Cell(_, 0)).toSet ++
    (0 until width).map(Cell(_, height - 1)).toSet))
  def addDeadBorder = copy(width + 2, height + 2, liveCells.map(c => Cell(c.x + 2, c.y + 2)))
  def apply(r: Rule) = copy(liveCells = (allCells.toSet.filter(c => neighborCount(c).
    |> (nc => (isAlive(c) && r.survive.contains(nc)) || (isDead(c) && r.born.contains(nc))))))
  def upscale = copy(width * 2, height * 2, liveCells.map(c => Cell(c.x * 2, c.y * 2)).flatMap(c =>
    Set(c, Cell(c.x + 1, c.y), Cell(c.x, c.y + 1), Cell(c.x + 1, c.y + 1)))) 
  lazy val potholes = allCells.collect(_ match {
    case c if isDead(c) && isAlive(c + Up) && isAlive(c + Down) => c
    case c if isDead(c) && isAlive(c + Left) && isAlive(c + Right) => c
    case c if isDead(c) && isAlive(c + UpLeft) && isAlive(c + DownRight) => c
    case c if isDead(c) && isAlive(c + UpRight) && isAlive(c + DownLeft) => c
  }).toSet
  def invert = copy(liveCells = allCells.toSet -- liveCells)
  
  // Returns a set of connected areas of live cells
  def areas: Set[Area] = {
    // Recursive flood fill
    // Returns the tuple (A, B) where A is the set of cells that were previously alive and are not
    // in B now; and B is the set of cells directly connected to c
    def fill(c: Cell, alive: Area, dead: Area): (Area, Area) =
      if (!(alive contains c)) (alive, dead) else
        fill(Cell(c.x - 1, c.y), (alive - c), (dead + c)).
        |> (n => fill(Cell(c.x + 1, c.y), n._1, n._2)).
        |> (n => fill(Cell(c.x, c.y - 1), n._1, n._2)).
        |> (n => fill(Cell(c.x, c.y + 1), n._1, n._2))
    
    // Returns the set of all connected areas in a
    def allAreas(a: Area, areas: Set[Area] = Set()): Set[Area] = if (a isEmpty) areas else
      fill(a head, a, Set()) |> (filled => allAreas(filled._1, areas + filled._2))
    
    allAreas(liveCells)
  }
  
  def border(a: Area) = a filter(neighborCount(_) < 8)
  def innerArea(a: Area) = a -- border(a) 
  def sortAreasBySize(a: Set[Area]): List[Area] = a.toList.sortBy(_.size)
  def findNeighborAreas(areas: List[Area]): Set[(Area, Area)] = {
    def grow(a: Area) = a flatMap(c => c.neighbors.toSet + c)
    // Grow the hd area until it intersects with one of the tl areas.
    // At that point, orig and the found tl area are considered neighbors.
    def intersection(orig: Area, hd: Area, tl: List[Area]): (Area, Area) =
      if (tl.size < 1) (orig, orig) else tl.find(_.intersect(hd).size > 0) |>
         (r => if (r.nonEmpty) (orig, r.get) else intersection(orig, grow(hd), tl))
    
    areas.foldLeft(Set[(Area, Area)]()) ((result, a) =>
      areas.drop(areas.indexOf(a)) |> (tl => result + intersection(tl.head, tl.head, tl.tail))
    ).filterNot(a => a._1 == a._2)
  }
  
  def printGrid {
    for (y <- 0 to height - 1) {
      for (x <- 0 to width - 1) {
        print (if (isAlive(Cell(x, y))) "#" else ".")
      }
      println
    }
  }
}

object GridTest extends App {
  val gol = new Rule(born = Set(3), survive = Set(2, 3))
  val gol2 = new Rule(born = Set(3), survive = Set(3, 4, 5, 6))
  val cave = new Rule(born = Set(6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
  val smooth = new Rule(born = Set(5, 6, 7, 8), survive = Set(3, 4, 5, 6, 7, 8))
  
  val ca = CellularAutomaton(50, 20).randomFill(0.5) (cave) (smooth) (smooth) (smooth) upscale (smooth)
  printGrid(ca)
  
  def printGrid(g: CellularAutomaton) {
    for (y <- 0 to g.height - 1) {
      for (x <- 0 to g.width - 1) {
        print (if (g.isAlive(Cell(x, y))) "#" else ".")
      }
      println
    }
  }
}
