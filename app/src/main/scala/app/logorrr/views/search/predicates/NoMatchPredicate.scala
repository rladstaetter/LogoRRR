package app.logorrr.views.search.predicates

import app.logorrr.clv.color.ColorMatcher

case class NoMatchPredicate(filters: Set[ColorMatcher]) extends LabelledFunction("Unclassified") {
  def apply(searchTerm: String): Boolean = !AnyMatchPredicate(filters)(searchTerm)
}
