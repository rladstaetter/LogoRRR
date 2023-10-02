package app.logorrr.views.search

import javafx.scene.paint.Color

class AnyFilter(filters: Set[Filter]) extends Fltr(Color.WHITE) {

  override val color: Color = {
    if (filters.isEmpty) {
      Color.WHITE
    } else if (filters.size == 1) {
      filters.head.color
    } else {
      filters.tail.foldLeft(filters.head.color)((acc, sf) => acc.interpolate(sf.color, 0.5))
    }
  }


  override def matches(searchTerm: String): Boolean = filters.exists(_.matches(searchTerm))
}
