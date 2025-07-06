package app.logorrr.views.search.predicates

import app.logorrr.jfxbfr.DescriptivePredicate

case class ContainsPredicate(pattern: String) extends DescriptivePredicate(pattern) {
  override def apply(searchTerm: String): Boolean = searchTerm.contains(pattern)
}
