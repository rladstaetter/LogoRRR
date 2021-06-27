package app.logorrr

import app.logorrr.views.LogColumnDef
import javafx.geometry.Insets
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.Color

import java.time.Instant

object LogEntry {

  def apply(value: String): LogEntry = LogEntry(value, None)

  def apply(value: String
            , colDef: LogColumnDef): LogEntry = {
    LogEntry(value, Option(colDef.parse(value)))
  }
}

/** represents one line in a log file */
case class LogEntry(value: String
                    , someInstant: Option[Instant]) {


  /**
   * calculate a color for this log entry.
   *
   * - either white if no search filter hits
   * - given color if only one hit
   * - a melange of all colors from all hits in all other cases
   * */
  def calcColor(searchFilters: Seq[Filter]): Color = {
    val hits = searchFilters.filter(sf => sf.applyMatch(value))
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

  /* pixel representation of rectangle to draw (mainly a performance optimisation) */
  def pixelArray(c: Color): Array[Int] = {
    ColorUtil.mkPixelArray(LogoRRRApp.InitialSquareWidth - 1, c)
  }

  def pixelArray(searchFilters: Seq[Filter]): Array[Int] = pixelArray(calcColor(searchFilters))

}
