package net.ladstatt.logorrr

import javafx.scene.paint.Color

object CaseInsensitiveFilter {

  def containsCaseInsensitive(string: String, needle: String): Boolean = string.toLowerCase.contains(needle.toLowerCase)

}

trait Filter {

  def title: String

  def color: Color

  def applyMatch(string: String): Boolean
}

/** encodes filter text and color which should be used to display it */
case class CaseInsensitiveFilter(value: String
                                 , color: Color) extends Filter {

  def applyMatch(string: String): Boolean = CaseInsensitiveFilter.containsCaseInsensitive(string, value)

  override def title: String = value
}

/** if none of the given filters match */
case class InverseFilter(filters: Set[Filter]) extends Filter {
  override def applyMatch(string: String): Boolean = !AtLeastOneMatchFilter(filters).applyMatch(string)

  override def color: Color = Color.WHITE

  override def title: String = "Unclassified"
}

/** if any of the provided filters match the searchstring */
case class AtLeastOneMatchFilter(filters: Set[Filter]) extends Filter {

  override def applyMatch(string: String): Boolean = filters.exists(s => s.applyMatch(string))

  override def color: Color = {
    if (filters.isEmpty) {
      Color.WHITE
    } else if (filters.size == 1) {
      filters.head.color
    } else {
      filters.tail.foldLeft(filters.head.color)((acc, sf) => acc.interpolate(sf.color, 0.5))
    }
  }

  override def title: String = "All"
}

