package app.logorrr.views

import app.logorrr.views.Filter.LMatcher
import javafx.scene.paint.Color
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}


class Filter(val value: String
             , val colorString: String) {

  val color: Color = Color.web(colorString)

  val matcher: LMatcher = Filter.CaseInsensitiveTextMatcher(value, color)

}

class UnclassifiedFilter(filters: Set[Filter]) extends Filter("Unclassified", Color.WHITE.toString) {
  override val matcher: LMatcher = new LMatcher(value) {

    override def color: Color = Color.web(colorString)

    /* no filter should match */
    override def applyMatch(string: String): Boolean = !filters.exists(_.matcher.applyMatch(string))
  }
}

class AnyFilter(filters: Set[Filter]) extends Filter("All", Color.WHITE.toString) {
  override val matcher: LMatcher = new LMatcher(value) {
    override val color: Color = {
      if (filters.isEmpty) {
        Color.WHITE
      } else if (filters.size == 1) {
        filters.head.color
      } else {
        filters.tail.foldLeft(filters.head.color)((acc, sf) => acc.interpolate(sf.color, 0.5))
      }
    }

    override def applyMatch(string: String): Boolean = filters.exists(_.matcher.applyMatch(string))
  }
}

object Filter {

  implicit lazy val reader = deriveReader[Filter]
  implicit lazy val writer = deriveWriter[Filter]

  val finest: Filter = new Filter("FINEST", Color.GREY.toString)
  val info: Filter = new Filter("INFO", Color.GREEN.toString)
  val warning: Filter = new Filter("WARNING", Color.ORANGE.toString)
  val severe: Filter = new Filter("SEVERE", Color.RED.toString)

  val seq: Seq[Filter] = Seq(finest, info, warning, severe)


  abstract class LMatcher(val value: String) {
    def color: Color

    def applyMatch(string: String): Boolean
  }

  case class CaseSensitiveTextMatcher(override val value: String, color: Color) extends LMatcher(value) {
    def applyMatch(string: String): Boolean = string.contains(value)
  }

  case class CaseInsensitiveTextMatcher(override val value: String, color: Color) extends LMatcher(value) {
    def applyMatch(string: String): Boolean = string.toLowerCase.contains(value.toLowerCase)
  }


  /**
   * calculate a color for this log entry.
   *
   * - either white if no search filter hits
   * - given color if only one hit
   * - a melange of all colors from all hits in all other cases
   * */
  def calcColor(value : String, filters: Seq[Filter]): Color = {
    val hits = filters.filter(sf => sf.matcher.applyMatch(value))
    val color = {
      if (hits.isEmpty) {
        Color.WHITE
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


