package app.logorrr.views.text

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import javafx.scene.control.Label
import javafx.scene.layout._
import javafx.scene.paint.Color

import scala.jdk.CollectionConverters._

/**
 * Represents a line in LogoRRR's TextView.
 *
 * A line consist of a line number to the left and the line contents to the right.
 *
 * @param e
 * @param maxLength
 * @param filters
 */
case class LogTextViewLabel(settings: MutLogFileSettings
                            , e: LogEntry
                            , maxLength: Int
                           ) extends HBox {

  lazy val stringsAndColor: Seq[(String, Color)] =
    FilterCalculator(e, settings.filtersProperty.get().asScala.toSeq).stringColorPairs

  lazy val labels: Seq[Label] = stringsAndColor.map {
    case (text, color) =>
      val l = LogoRRRLabel.mkL(text, color)
      l.styleProperty().bind(settings.fontStyle)
      l
  }

  val lineNumberLabel: LineNumberLabel = {
    val l = LineNumberLabel(e.lineNumber, maxLength)
    l.styleProperty().bind(settings.fontStyle)
    l
  }

  getChildren.add(lineNumberLabel)
  getChildren.addAll(labels: _*)

}


