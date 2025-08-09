package app.logorrr.views.search.filter

import app.logorrr.clv.color.ColorMatcher
import app.logorrr.views.MutFilter
import app.logorrr.views.search.predicates.AnyMatchPredicate
import javafx.scene.paint.Color


class AnyFilter(filters: Set[ColorMatcher]) extends MutFilter {

  init(AnyMatchPredicate(filters), Color.WHITE, active = true)

  override val getColor: Color = {
    if (filters.isEmpty) {
      Color.WHITE
    } else if (filters.size == 1) {
      filters.head.getColor
    } else {
      filters.tail.foldLeft(filters.head.getColor)((acc, sf) => acc.interpolate(sf.getColor, 0.5))
    }
  }

}
