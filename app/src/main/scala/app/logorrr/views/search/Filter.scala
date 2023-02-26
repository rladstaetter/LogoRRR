package app.logorrr.views.search

import javafx.scene.paint.Color
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}
import pureconfig.{ConfigReader, ConfigWriter}

object Filter {

  // Filter has javafx Color in it's constructor, we need to define explicit mappings
  implicit lazy val colorReader: ConfigReader[Color] = ConfigReader[String].map(s => Color.web(s))
  implicit lazy val colorWriter: ConfigWriter[Color] = ConfigWriter[String].contramap(c => c.toString)

  implicit lazy val reader = deriveReader[Filter]
  implicit lazy val writer = deriveWriter[Filter]


  val unClassifiedFilterColor = Color.LIGHTGREY

  /**
   * calculate a color for this log entry.
   *
   * - either white if no search filter hits
   * - given color if only one hit
   * - a melange of all colors from all hits in all other cases
   * */
  def calcColor(value: String, filters: Seq[Fltr]): Color = {
    val hits = filters.filter(sf => sf.applyMatch(value))
    val color = {
      if (hits.isEmpty) {
        Color.LIGHTGREY
      } else if (hits.size == 1) {
        hits.head.color
      } else {
        val c = hits.tail.foldLeft(hits.head.color)((acc, sf) => acc.interpolate(sf.color, 0.5))
        c
      }
    }
    color
  }

}

/**
 * Pairs a searchterm to a color.
 *
 * The idea is to encode each search term with a color such that one can immediately spot an occurence in the views.
 *
 * @param pattern text to search for
 * @param color associated color
 */
// TODO write encoder for pureconfig for color, see https://github.com/rladstaetter/LogoRRR/issues/105
class Filter(val pattern: String, val color: Color) extends Fltr {

  override def applyMatch(searchTerm: String): Boolean = searchTerm.contains(pattern)
}