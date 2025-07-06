package app.logorrr.views.search

import app.logorrr.jfxbfr.Fltr
import javafx.scene.paint.Color

case class AnyMatch(filters: Set[Fltr[_]]) extends Function1[String, Boolean] {
  override def apply(searchTerm: String): Boolean = filters.exists(_.matches(searchTerm))
}

class AnyFilter(filters: Set[Fltr[_]]) extends Fltr {
  init(AnyMatch(filters), "any", Color.WHITE, active = true)

  override val getColor: Color = {
    if (filters.isEmpty) {
      Color.WHITE
    } else if (filters.size == 1) {
      filters.head.getColor
    } else {
      filters.tail.foldLeft(filters.head.getColor)((acc, sf) => acc.interpolate(sf.getColor, 0.5))
    }
  }

  override def matches(searchTerm: String): Boolean = filters.exists(_.matches(searchTerm))
}
