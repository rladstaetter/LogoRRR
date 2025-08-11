package app.logorrr.views.search.filter

import app.logorrr.clv.color.ColorMatcher
import app.logorrr.views.MutFilter
import app.logorrr.views.search.predicates.NoMatchPredicate
import javafx.scene.paint.Color

object UnclassifiedFilter {

  val color: Color = Color.LIGHTGREY

}


/**
 * Match everything except given filters
 *
 * @param filters a set of filters to build the complement
 */
case class UnclassifiedFilter(filters: Set[ColorMatcher]) extends MutFilter {
  init(NoMatchPredicate(filters), UnclassifiedFilter.color, active = true)
}
