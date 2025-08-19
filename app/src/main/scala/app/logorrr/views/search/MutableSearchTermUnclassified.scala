package app.logorrr.views.search

import app.logorrr.clv.color.ColorMatcher
import app.logorrr.views.MutableSearchTerm
import app.logorrr.views.search.predicates.NoMatchPredicate
import javafx.scene.paint.Color

object MutableSearchTermUnclassified {

  val color: Color = Color.LIGHTGREY

}


/**
 * Match everything except given filters
 *
 * @param filters a set of filters to build the complement
 */
case class MutableSearchTermUnclassified(filters: Set[ColorMatcher]) extends MutableSearchTerm {
  init(NoMatchPredicate(filters), MutableSearchTermUnclassified.color, active = true)
}
