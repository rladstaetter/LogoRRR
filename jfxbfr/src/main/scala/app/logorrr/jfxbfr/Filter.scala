package app.logorrr.jfxbfr

import javafx.scene.paint.Color
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}
import pureconfig.{ConfigReader, ConfigWriter}

object Filter {

  implicit lazy val colorReader: ConfigReader[Color] = ConfigReader[String].map(s => Color.web(s))
  implicit lazy val colorWriter: ConfigWriter[Color] = ConfigWriter[String].contramap(c => c.toString)

  implicit lazy val reader: ConfigReader[Filter] = deriveReader[Filter]
  implicit lazy val writer: ConfigWriter[Filter] = deriveWriter[Filter]

}

/**
 * Pairs a searchterm to a color.
 *
 * The idea is to encode each search term with a color such that one can immediately spot an occurence in the views.
 *
 * @param pattern text to search for
 * @param color   associated color
 * @param active  is filter active
 */
case class Filter(pattern: String
                  , color: Color
                  , active: Boolean)

