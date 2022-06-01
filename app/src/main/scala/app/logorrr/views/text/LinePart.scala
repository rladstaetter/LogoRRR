package app.logorrr.views.text

import javafx.scene.paint.Color

object LinePart {

  def reduce(string: String, ps: Seq[LinePart]): Seq[LinePart] = {
    val parts = ps.sortWith((a, b) => a.start < b.start)
    if (parts.isEmpty) {
      Seq(LinePart(string, 0))
    } else {
      val untilLast =
        parts.foldLeft(Seq[LinePart]()) {
          case (acc, p) =>
            val startIndex = if (acc.isEmpty) 0 else acc.last.end
            if (startIndex == p.start) {
              acc ++ Seq(p)
            } else {
              acc ++ Seq(LinePart(string.substring(startIndex, p.start), startIndex), p)
            }
        }
      if (untilLast.last.end == string.length) {
        untilLast
      } else {
        untilLast ++ Seq(LinePart(string.substring(untilLast.last.end, string.length), untilLast.last.end))
      }
    }
  }


  def apply(value: String, start: Int): LinePart = LinePart(value, start, None)
}

case class LinePart(value: String
                    , start: Int
                    , someColor: Option[Color]) {
  val end = start + value.length
}