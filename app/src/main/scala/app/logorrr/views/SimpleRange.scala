package app.logorrr.views

import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

object SimpleRange {

  implicit lazy val reader = deriveReader[SimpleRange]
  implicit lazy val writer = deriveWriter[SimpleRange]

}

case class SimpleRange(start: Int, end: Int) {
  require(start <= end)
  val length = end - start
}