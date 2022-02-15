package app.logorrr.model

import app.logorrr.views.Filter
import javafx.geometry.Insets
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.Color

import java.time.Instant

object LogEntry {

  def apply(lineNumber: Long, value: String): LogEntry = LogEntry(lineNumber, value, None)

}

/**
 * represents one line in a log file
 *
 * @param lineNumber line number of this log entry
 * @param value contens of line in plaintext
 * @param someInstant a timestamp if there is any
 * */
case class LogEntry(lineNumber: Long
                    , value: String
                    , someInstant: Option[Instant]) {


  /**
   * calculate a color for this log entry.
   *
   * - either white if no search filter hits
   * - given color if only one hit
   * - a melange of all colors from all hits in all other cases
   * */
  def calcColor(filters: Seq[Filter]): Color = {
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

  def background(searchFilters: Seq[Filter]): Background =
    new Background(new BackgroundFill(calcColor(searchFilters), new CornerRadii(1.0), new Insets(0.0)))


}
