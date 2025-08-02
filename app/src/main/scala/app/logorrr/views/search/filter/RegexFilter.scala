package app.logorrr.views.search.filter

import app.logorrr.views.MutFilter
import app.logorrr.views.search.predicates.RegexPredicate
import javafx.scene.paint.Color

case class RegexFilter(pattern: String
                       , color: Color
                       , active: Boolean) extends MutFilter {

  init(RegexPredicate(pattern), color, active)
}

