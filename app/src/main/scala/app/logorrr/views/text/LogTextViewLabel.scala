package app.logorrr.views.text

import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import app.logorrr.views.search.Filter
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.paint.Color

/**
 * Represents a line in LogoRRR's TextView.
 *
 * A line consist of a line number to the left and the line contents to the right.
 *
 * @param e
 * @param maxLength
 * @param filters
 */
case class LogTextViewLabel(e: LogEntry
                            , maxLength: Int
                            , filters: Seq[Filter]
                           ) extends HBox {

  lazy val stringsAndColor: Seq[(String, Color)] = FilterCalculator(e, filters).stringColorPairs

  lazy val labels: Seq[Label] = stringsAndColor.map {
    case (text, color) => LogoRRRLabel.mkL(text, color)
  }

  val lineNumberLabel: LineNumberLabel = LineNumberLabel(e.lineNumber, maxLength)
  getChildren.add(lineNumberLabel)
  getChildren.addAll(labels: _*)

}
