package de.atextor.madv.engine

// Taken from http://stackoverflow.com/questions/3640823/what-type-to-use-to-store-an-in-memory-mutable-data-table-in-scala
// Original author: Aaron Novstrup
class Memoize1[-T, +R](f: T => R) extends (T => R) {
   import scala.collection.mutable
   private[this] val vals = mutable.Map.empty[T, R]
   def apply(x: T): R = vals getOrElseUpdate (x, f(x))
}

object Memoize {
   def memoize[T, R](f: T => R): (T => R) = new Memoize1(f)

   def memoize[T1, T2, R](f: (T1, T2) => R): ((T1, T2) => R) = 
      Function.untupled(memoize(f.tupled))

   def memoize[T1, T2, T3, R](f: (T1, T2, T3) => R): ((T1, T2, T3) => R) =
      Function.untupled(memoize(f.tupled))

   // ... more memoize methods for higher-arity functions ...

   def Y[T, R](f: (T => R) => T => R): (T => R) = {
      lazy val yf: (T => R) = memoize(f(yf)(_))
      yf
   }
}
