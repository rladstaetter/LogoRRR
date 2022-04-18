package app.logorrr.model

import app.logorrr.views.Filter
import app.logorrr.views.block.BlockView
import javafx.geometry.Insets
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.Color

import java.time.Instant
import scala.language.postfixOps

object LogEntry {


  def apply(lineNumber: Int, value: String): LogEntry = LogEntry(lineNumber, Color.RED, value, None)

}

/**
 * represents one line in a log file
 *
 * @param lineNumber line number of this log entry
 * @param value contens of line in plaintext
 * @param someInstant a timestamp if there is any
 * */
case class LogEntry(lineNumber: Int
                    , color: Color
                    , value: String
                    , someInstant: Option[Instant]) extends BlockView.E {

  val index: Int = lineNumber

  def background(searchFilters: Seq[Filter]): Background =
    new Background(new BackgroundFill(Filter.calcColor(value, searchFilters), new CornerRadii(1.0), new Insets(0.0)))


}
