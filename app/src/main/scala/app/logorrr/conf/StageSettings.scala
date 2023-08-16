package app.logorrr.conf

import pureconfig.{ConfigReader, ConfigWriter}
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}



object StageSettings {

  implicit val reader: ConfigReader[StageSettings] = deriveReader[StageSettings]
  implicit val writer: ConfigWriter[StageSettings] = deriveWriter[StageSettings]


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