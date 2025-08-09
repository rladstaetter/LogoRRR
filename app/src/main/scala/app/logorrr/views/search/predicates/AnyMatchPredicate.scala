package app.logorrr.views.search.predicates

import app.logorrr.clv.color.ColorMatcher

case class AnyMatchPredicate(filters: Set[ColorMatcher]) extends DescriptivePredicate("any") {
  override def apply(searchTerm: String): Boolean = filters.exists(_.matches(searchTerm))
}
