package de.atextor.madv.engine

import scala.language.implicitConversions
import java.util.concurrent.TimeUnit._
import java.util.concurrent.TimeUnit

object Util {
  implicit def pipelineSyntax[A](a: => A) = new { def |>[B](f: A => B) = f(a) }
  
  case class Duration(duration: Long, unit: TimeUnit) {
    def toDays = DAYS.convert(duration, unit)
    def toHours = HOURS.convert(duration, unit)
    def toMicros = MICROSECONDS.convert(duration, unit)
    def toMillis = MILLISECONDS.convert(duration, unit)
    def toMinutes = MINUTES.convert(duration, unit)
    def toNanos = NANOSECONDS.convert(duration, unit)
    def toSeconds = SECONDS.convert(duration, unit)
  }
  implicit def intToDuration(i: Int) = new { def ms = Duration(i, MILLISECONDS) }
}
