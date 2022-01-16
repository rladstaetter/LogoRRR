package app.logorrr.conf

import pureconfig.generic.semiauto.{deriveReader, deriveWriter}



object StageSettings {

  implicit val reader = deriveReader[StageSettings]
  implicit val writer = deriveWriter[StageSettings]


}

/**
 * @param x upper left x cooordinate for Logorrr
 * @param y upper left y cooordinate for Logorrr
 * @param width width of stage
 * @param height height of stage
 */
case class StageSettings(x: Double
                         , y: Double
                         , width: Int
                         , height: Int)