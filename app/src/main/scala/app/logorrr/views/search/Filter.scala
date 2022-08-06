package app.logorrr.views.search

import app.logorrr.views.search.Filter.LMatcher
import javafx.scene.paint.Color
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

object Filter {

  implicit lazy val reader = deriveReader[Filter]
  implicit lazy val writer = deriveWriter[Filter]

  val unClassifiedFilterColor = Color.LIGHTGREY

  abstract class LMatcher {

    def color: Color

    def applyMatch(searchTerm: String): Boolean
  }

  /** matches if at least one filter matches */
  case class ExistsMatcher(filters: Set[Filter], color: Color) extends LMatcher {
    override def applyMatch(searchTerm: String): Boolean = {
      filters.exists(_.matcher.applyMatch(searchTerm))
    }
  }

  /** matches if no filter matches */
  case class NotExistsMatcher(filters: Set[Filter], color: Color) extends LMatcher {

    val existsMatcher = ExistsMatcher(filters, color)

    override def applyMatch(searchTerm: String): Boolean = !existsMatcher.applyMatch(searchTerm)
  }

  /** matches if value matches search term (case insensitive) */
  case class CaseInsensitiveTextMatcher(value: String, color: Color) extends LMatcher {
    def applyMatch(searchTerm: String): Boolean = searchTerm.toLowerCase.contains(value.toLowerCase)
  }

  /**
   * calculate a color for this log entry.
   *
   * - either white if no search filter hits
   * - given color if only one hit
   * - a melange of all colors from all hits in all other cases
   * */
  def calcColor(value: String, filters: Seq[Fltr]): Color = {
    val hits = filters.filter(sf => sf.matcher.applyMatch(value))
    val color = {
      if (hits.isEmpty) {
        Color.LIGHTGREY
      } else if (hits.size == 1) {
        hits.head.color
      } else {
        val c = hits.tail.foldLeft(hits.head.color)((acc, sf) => acc.interpolate(sf.color, 0.5))
        c
      }
    }
    color
  }
}

/**
 * Pairs a searchterm to a color.
 *
 * The idea is to encode each search term with a color such that one can immediately spot an occurence in the views.
 *
 * @param pattern text to search for
 * @param colorString associated color
 */
// TODO write encoder for pureconfig for color
class Filter(val pattern: String
             , val colorString: String) extends Fltr {

  val color: Color = Color.web(colorString)

  val matcher: LMatcher = Filter.CaseInsensitiveTextMatcher(pattern, color)

}