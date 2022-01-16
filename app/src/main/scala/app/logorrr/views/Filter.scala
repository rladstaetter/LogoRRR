package app.logorrr.views

import app.logorrr.views.Filter.Matcher
import javafx.scene.paint.Color
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}


class Filter(val value: String
             , val colorString: String) {

  val color: Color = Color.web(colorString)

  val matcher: Matcher = Filter.ContainsMatcher(value, color)

}

class UnclassifiedFilter(filters: Set[Filter]) extends Filter("Unclassified", Color.WHITE.toString) {
  override val matcher: Matcher = new Matcher(value) {

    override def color: Color = Color.web(colorString)

    /* no filter should match */
    override def applyMatch(string: String): Boolean = !filters.exists(_.matcher.applyMatch(string))
  }
}

class AnyFilter(filters: Set[Filter]) extends Filter("All", Color.WHITE.toString) {
  override val matcher: Matcher = new Matcher(value) {
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


  abstract class Matcher(val value: String) {
    def color: Color

    def applyMatch(string: String): Boolean
  }

  case class ContainsMatcher(override val value: String, color: Color) extends Matcher(value) {
    def applyMatch(string: String): Boolean = string.contains(value)
  }


}


