package app.logorrr.views.text

import app.logorrr.views.search.Filter
import javafx.scene.paint.Color

object LinePart {

  def apply(value: String, start: Int): LinePart = LinePart(value, start, Filter.unClassifiedFilterColor)
}

/**
 * A subset of a line with a certain string value and a startposition, associated with a color.
 *
 * @param value
 * @param startIndex
 * @param color
 */
case class LinePart(value: String
                    , startIndex: Int
                    , color: Color) {

  require(value.nonEmpty)

  val endIndex = startIndex + value.length - 1

  def hit(index: Int): Boolean = (startIndex <= index) && (index <= endIndex)

}