package app.logorrr.views.text

import app.logorrr.model.LogEntry
import app.logorrr.util.JetbrainsMonoFontStyleBinding
import javafx.beans.property.{Property, ReadOnlyDoubleProperty}
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color

object LogTextViewLabel:

  def bindProperties(l: Label, heightProperty: ReadOnlyDoubleProperty, fontSizeProperty: Property[Number]): Unit =
    l.minHeightProperty().bind(heightProperty)
    l.maxHeightProperty().bind(heightProperty)
    l.styleProperty().bind(new JetbrainsMonoFontStyleBinding(fontSizeProperty))

  def unbind(logTextViewLabel: LogTextViewLabel): Unit = {
    logTextViewLabel.getChildren.forEach {
      case l: Label => unbind(l)
      case _ =>
    }
  }

  def unbind(l: Label): Unit = {
    l.minHeightProperty().unbind()
    l.maxHeightProperty().unbind()
    l.styleProperty().unbind()
  }


/**
 * Represents a line in LogoRRR's TextView.
 *
 * A line consist of a line number to the left and the line contents to the right.
 */
class LogTextViewLabel(e: LogEntry
                       , maxLength: Int
                       , searchTerms: Seq[(String, Color)]
                       , fontSizeProperty: Property[Number]
                      ) extends HBox:

  setHeight(fontSizeProperty.getValue.intValue())
  setAlignment(Pos.CENTER_LEFT)

  val stringsAndColor: Seq[(String, Color)] = SearchTermCalculator(e.value, searchTerms).stringColorPairs

  val lineNumberLabel: LineNumberLabel =
    val l = LineNumberLabel(e.lineNumber, maxLength)
    LogTextViewLabel.bindProperties(l, heightProperty, fontSizeProperty)
    l

  val labels: Seq[Label] = stringsAndColor.map:
    case (text, color) =>
      val l = LogoRRRLabel.mkL(text, color)
      LogTextViewLabel.bindProperties(l, heightProperty, fontSizeProperty)
      l

  getChildren.add(lineNumberLabel)
  getChildren.addAll(labels *)

  def getLogEntry: LogEntry = e

