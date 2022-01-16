package app.logorrr.conf

import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

object SquareImageSettings {

  implicit lazy val reader = deriveReader[SquareImageSettings]
  implicit lazy val writer = deriveWriter[SquareImageSettings]

}
/**
 * @param width width of rectangles
 */
case class SquareImageSettings(width: Int)
