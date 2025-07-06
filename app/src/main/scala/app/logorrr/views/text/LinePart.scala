package app.logorrr.views.text

import app.logorrr.views.search.UnclassifiedFilter
import javafx.scene.paint.Color

object LinePart {

  def apply(value: String, start: Int): LinePart = LinePart(value, start, UnclassifiedFilter.unClassifiedFilterColor)
}

/**
 * A subset of a line with a certain string value and a startposition, associated with a color.
 *
 * @param value      a part of a log line
 * @param startIndex start index of this part
 * @param color      color to display
 */
case class LinePart(value: String
                    , startIndex: Int
                    , color: Color) {

  require(value.nonEmpty)

  val endIndex: Int = startIndex + value.length - 1

  def hit(index: Int): Boolean = (startIndex <= index) && (index <= endIndex)

}