package app.logorrr.views.search

import app.logorrr.jfxbfr.Fltr
import javafx.scene.paint.Color

object UnclassifiedFilter {

  val unClassifiedFilterColor: Color = Color.LIGHTGREY

}

case class NoMatch(filters : Set[Fltr[_]]) extends Function1[String,Boolean] {
  def apply(searchTerm:String) :Boolean = !filters.exists(_.matches(searchTerm))
}
/**
 * Match everything except given filters
 *
 * @param filters a set of filters to build the complement
 */
case class UnclassifiedFilter(filters: Set[Fltr[_]]) extends Fltr {
  init(NoMatch(filters), "Unclassified", UnclassifiedFilter.unClassifiedFilterColor, active = true)

  override def matches(searchTerm: String): Boolean = !filters.exists(_.matches(searchTerm))

}
