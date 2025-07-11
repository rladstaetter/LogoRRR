package app.logorrr.views.search.predicates

import app.logorrr.jfxbfr.DescriptivePredicate
import app.logorrr.views.MutFilter

case class NoMatchPredicate(filters: Set[MutFilter[_]]) extends DescriptivePredicate("Unclassified") {
  def apply(searchTerm: String): Boolean = !filters.exists(_.matches(searchTerm))
}
