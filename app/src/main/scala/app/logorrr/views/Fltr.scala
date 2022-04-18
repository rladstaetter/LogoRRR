package app.logorrr.views

import app.logorrr.views.Filter.{ExistsMatcher, LMatcher, NotExistsMatcher}
import javafx.scene.paint.Color
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}



trait Fltr {

  val color: Color

  val matcher: LMatcher

}



class UnclassifiedFilter(filters: Set[Filter]) extends Filter("Unclassified", Filter.unClassifiedFilterColor.toString) {

  override val matcher = NotExistsMatcher(filters, Filter.unClassifiedFilterColor)

}

class AnyFilter(filters: Set[Filter]) extends Fltr {

  override val color: Color = {
    if (filters.isEmpty) {
      Color.WHITE
    } else if (filters.size == 1) {
      filters.head.color
    } else {
      filters.tail.foldLeft(filters.head.color)((acc, sf) => acc.interpolate(sf.color, 0.5))
    }
  }

  override val matcher: LMatcher = ExistsMatcher(filters, color)

}


