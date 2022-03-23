package app.logorrr.conf.mut

/** Marker interface for mutable objects which have an immutable representation */
trait Petrify[I] {
  def petrify(): I
}
