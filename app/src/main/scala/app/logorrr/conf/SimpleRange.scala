package app.logorrr.conf

import upickle.default.*

object SimpleRange {

}

case class SimpleRange(start: Int, end: Int) derives ReadWriter {
  require(start <= end, s"Expected start <= end, but was $start <= $end")
  val length: Int = end - start
}