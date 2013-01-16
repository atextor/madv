package de.atextor.madv.engine

import scala.language.implicitConversions
import java.util.concurrent.TimeUnit._
import java.util.concurrent.TimeUnit

object Util {
  class PipelineSyntax[A](a: => A) {
    def |>[B](f: A => B) = f(a)
  }
  implicit def pipelineSyntax[A](a: => A) = new PipelineSyntax(a)
  
  case class Duration(duration: Long, unit: TimeUnit) {
    def toNanos = NANOSECONDS.convert(duration, unit)
    def toMicros = MICROSECONDS.convert(duration, unit)
    def toMillis = MILLISECONDS.convert(duration, unit)
    def toSeconds = SECONDS.convert(duration, unit)
    def toMinutes = MINUTES.convert(duration, unit)
    def toHours = HOURS.convert(duration, unit)
    def toDays = DAYS.convert(duration, unit)
  }
  class DurationLong(l: Long) {
    def ns = Duration(l, NANOSECONDS)
    def µs = Duration(l, MICROSECONDS)
    def ms = Duration(l, MILLISECONDS)
    def sec = Duration(l, SECONDS)
    def minutes = Duration(l, MINUTES)
    def hours = Duration(l, HOURS)
    def days = Duration(l, DAYS)
  }
  implicit def intToDurationInt(i: Int) = new DurationLong(i)
  implicit def longToDurationLong(l: Long) = new DurationLong(l)
}
