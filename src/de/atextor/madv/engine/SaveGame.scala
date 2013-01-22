package de.atextor.madv.engine

// Uses the "serializable" type class
object SaveGame {
  trait Serializable[T] {
    def ser(t: T): Unit
  }

  def serialize[T](t: T)(implicit s: Serializable[T]) = s.ser(t)

  implicit class SerializeOps[T](val t: T)(implicit s: Serializable[T]) {
    def serialize = s.ser(t)
  }

  // Concrete serializers
  
  implicit object SerializeEntity extends Serializable[Entity] {
    def ser(e: Entity) {
      // TODO
    }
  }
}