package app.logorrr.jfxbfr

/**
 * Implement this function to define what action is performed if an element is selected
 * @tparam A
 */
trait ElementSelector[A] {
  def select(a: A): Unit
}
