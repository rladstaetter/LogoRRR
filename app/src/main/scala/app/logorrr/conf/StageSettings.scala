package app.logorrr.conf

import javafx.geometry.Rectangle2D
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}
import pureconfig.{ConfigReader, ConfigWriter}



object StageSettings {

  implicit val reader: ConfigReader[StageSettings] = deriveReader[StageSettings]
  implicit val writer: ConfigWriter[StageSettings] = deriveWriter[StageSettings]

  def apply(rectangle2D: Rectangle2D): StageSettings = {
    StageSettings(rectangle2D.getMinX, rectangle2D.getMinY, rectangle2D.getWidth.toInt, rectangle2D.getHeight.toInt)
  }

}

/**
 * @param x      upper left x cooordinate for Logorrr
 * @param y      upper left y cooordinate for Logorrr
 * @param width  width of stage
 * @param height height of stage
 */
case class StageSettings(x: Double
                         , y: Double
                         , width: Int
                         , height: Int)