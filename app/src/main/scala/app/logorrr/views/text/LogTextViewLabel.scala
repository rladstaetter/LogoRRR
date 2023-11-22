package app.logorrr.views.text

import app.logorrr.model.LogEntry
import app.logorrr.views.search.Filter
import javafx.beans.binding.StringBinding
import javafx.beans.property.IntegerProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout._
import javafx.scene.paint.Color

/**
 * Represents a line in LogoRRR's TextView.
 *
 * A line consist of a line number to the left and the line contents to the right.
 */
case class LogTextViewLabel(e: LogEntry
                            , maxLength: Int
                            , filters: Seq[Filter]
                            , fontStyleBinding: StringBinding
                            , fontSizeProperty: IntegerProperty
                           ) extends HBox {

  setHeight(fontSizeProperty.get())
  setAlignment(Pos.CENTER_LEFT)

  val stringsAndColor: Seq[(String, Color)] = FilterCalculator(e, filters).stringColorPairs

  val labels: Seq[Label] = stringsAndColor.map {
    case (text, color) =>
      val l = LogoRRRLabel.mkL(text, color)
      l.minHeightProperty().bind(heightProperty())
      l.maxHeightProperty().bind(heightProperty())
      l.styleProperty().bind(fontStyleBinding)
      l
  }

  val lineNumberLabel: LineNumberLabel = {
    val l = LineNumberLabel(e.lineNumber, maxLength)
    l.minHeightProperty().bind(heightProperty())
    l.maxHeightProperty().bind(heightProperty())
    l.styleProperty().bind(fontStyleBinding)
    l
  }

  e.someInstant.foreach(
    i => getChildren.add(LineTimerLabel(i))
  )

  getChildren.add(lineNumberLabel)
  getChildren.addAll(labels: _*)

}


