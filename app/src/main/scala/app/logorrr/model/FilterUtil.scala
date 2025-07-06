package app.logorrr.model

import app.logorrr.jfxbfr.MutFilter
import app.logorrr.views.search.predicates.ContainsPredicate
import javafx.scene.paint.Color

object FilterUtil {

  /** Default mapping for filters */
  val DefaultFilters: Seq[MutFilter[String]] = Map(
    "FINEST" -> Color.GREY
    , "INFO" -> Color.GREEN
    , "WARNING" -> Color.ORANGE
    , "SEVERE" -> Color.RED
  ).map {
    case (pattern, color) => MutFilter[String](ContainsPredicate(pattern), color, active = true)
  }.toSeq

}
