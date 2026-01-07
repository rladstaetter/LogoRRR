package app.logorrr.conf

import upickle.default._

object SimpleRange {
  implicit lazy val rw: ReadWriter[SimpleRange] = macroRW
}

case class SimpleRange(start: Int, end: Int) {
  require(start <= end, s"Expected start <= end, but was $start <= $end")
  val length: Int = end - start
}