package app.logorrr.views.search.predicates

import app.logorrr.views.MutFilter

case class AnyMatchPredicate(filters: Set[MutFilter[_]]) extends DescriptivePredicate("any") {
  override def apply(searchTerm: String): Boolean = filters.exists(_.matches(searchTerm))
}
