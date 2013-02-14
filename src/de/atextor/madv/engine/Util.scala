package de.atextor.madv.engine

import scala.language.implicitConversions

object Util {
  class PipelineSyntax[A](a: => A) {
    def |>[B](f: A => B) = f(a)
  }
  implicit def pipelineSyntax[A](a: => A) = new PipelineSyntax(a)
}
