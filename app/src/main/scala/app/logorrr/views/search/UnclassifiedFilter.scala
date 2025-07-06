package app.logorrr.views.search

import app.logorrr.jfxbfr.Fltr
import javafx.scene.paint.Color

object UnclassifiedFilter {

  val unClassifiedFilterColor: Color = Color.LIGHTGREY

}

/**
 * Match everything except given filters
 *
 * @param filters a set of filters to build the complement
 */
case class UnclassifiedFilter(filters: Set[Fltr]) extends Fltr {
  init("Unclassified", UnclassifiedFilter.unClassifiedFilterColor, active = true)

  override def matches(searchTerm: String): Boolean = !filters.exists(_.matches(searchTerm))

}
