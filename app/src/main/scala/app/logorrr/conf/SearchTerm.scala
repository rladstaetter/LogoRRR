package app.logorrr.conf

import app.logorrr.views.search.MutableSearchTerm
import javafx.scene.paint.Color
import upickle.default.*

object SearchTerm:

  // 1. Define the Color mapping (Hex String <-> JavaFX Color)
  implicit val colorRW: ReadWriter[Color] = readwriter[String].bimap[Color](
    c => {
      // Convert Color to Hex String (e.g., 0x112233ff -> "#112233")
      f"#${(c.getRed * 255).toInt}%02x${(c.getGreen * 255).toInt}%02x${(c.getBlue * 255).toInt}%02x"
    },
    s => {
      // Convert Hex String back to JavaFX Color
      Color.web(s)
    }
  )


  /**
   * Determines if a given line should be filtered out based on a set of search terms.
   *
   * This function returns `true` if the line should be kept (i.e., it doesn't match
   * any active search terms), and `false` if it should be filtered out (i.e., it
   * matches at least one active search term).
   *
   * The matching is case-sensitive. The function ignores inactive search terms and
   * any `color` attributes.
   *
   * @param line        The string to be checked against the search terms.
   * @param searchTerms A set of [[SearchTerm]] objects.
   * @return `true` if `searchTerms` is empty or if no active search term's `value` is
   *         found within the `line`. Returns `false` otherwise.
   */
  def dontMatch(line: String, searchTerms: Set[SearchTerm]): Boolean =
    val activeSearchTerms = searchTerms // .filter(_.active)
    if activeSearchTerms.isEmpty then
      true
    else
      !activeSearchTerms.filter(s => Option(s.value).isDefined).exists(v => line.contains(v.value))

  /**
   * Returns true if the element string contains any of the active search terms.
   *
   * This function first filters the set of search terms to find only those that are active
   * (where the `Boolean` flag is `true`). It then checks if the provided `element` string
   * contains at least one of these active search terms. The check is performed using regular
   * expression matching.
   *
   * @param element     The string to be checked for matching content.
   * @param searchTerms A set of tuples, where each tuple contains a search string, its associated color,
   *                    and a `Boolean` flag to activate or deactivate the search string.
   * @return `true` if any active search term is found within the element, `false` otherwise.
   */
  def matches(element: String, searchTerms: Set[SearchTerm]): Boolean =
    // Filter for active search terms
    val activeSearchTerms: Set[SearchTerm] = searchTerms.filter(_.active)

    if activeSearchTerms.isEmpty then
      false
    else
      // Check if the element contains any of the active search terms
      activeSearchTerms.exists { case SearchTerm(searchTerm, _, _) =>
        // Handle the case of an empty search string which matches everything
        if searchTerm.isEmpty then
          true
        else
          // Use regex to check for a match
          searchTerm.r.findFirstIn(element).isDefined
      }

  /**
   * Given a string 'element', returns an interpolated color based on a set of search strings.
   *
   * The function counts the occurrences of each search string within the element string. If
   * either the element or the set of search strings is empty, it returns `ColorMatcher.Unclassified`.
   *
   * Otherwise, it calculates a weighted interpolated color. The weight for each color is determined
   * by the number of times its corresponding search string appears in the element string. The final
   * color is the normalized sum of all weighted colors. If no search strings are found, the result
   * is `ColorMatcher.Unclassified`.
   *
   * @param element       The string to be analyzed for search string occurrences.
   * @param searchStrings A set of tuples, where each tuple contains a search string and its associated color.
   * @return A `Color` interpolated from the colors of the matched search strings, or `ColorMatcher.Unclassified`.
   */
  def calc(element: String, searchStrings: Set[SearchTerm]): Color =
    // Filter for active search strings
    val activeSearchStrings = searchStrings.filter(_.active)

    // If there are no active search strings, return Unclassified
    if element.isEmpty || activeSearchStrings.isEmpty then
      MutableSearchTerm.UnclassifiedColor
    else
      // Map each active search string to its occurrence count
      val counts = activeSearchStrings.map { case SearchTerm(str, color, _) =>
        val count = str.r.findAllIn(element).length
        (color, count)
      }.toMap

      // Check if any active search strings were found
      val countsSum = counts.values.sum

      if countsSum == 0 then
        MutableSearchTerm.UnclassifiedColor
      else if countsSum == 1 then
        // performance optimisation - most of the time only one search term is found per line
        // no interpolation has to be performed
        counts.filter { case (_, count) => count == 1 }.head._1
      else

        // Calculate the weighted sum of colors
        val (redSum, greenSum, blueSum) = counts.foldLeft((0.0, 0.0, 0.0)):
          case ((r, g, b), (color, count)) =>
            (r + color.getRed * count, g + color.getGreen * count, b + color.getBlue * count)

        val totalCount = countsSum.toDouble

        // Normalize and create the interpolated color
        val interpolatedRed = redSum / totalCount
        val interpolatedGreen = greenSum / totalCount
        val interpolatedBlue = blueSum / totalCount

        // Create the final color, ensuring components are within the valid [0, 1] range
        Color.color(
          interpolatedRed.max(0).min(1),
          interpolatedGreen.max(0).min(1),
          interpolatedBlue.max(0).min(1)
        )

/**
 * Pairs a searchterm to a color.
 *
 * The idea is to encode each search term with a color such that one can immediately spot an occurence in the views.
 *
 * @param value  text to search for
 * @param color  associated color
 * @param active is filter active
 */
case class SearchTerm(value: String
                      , color: Color
                      , active: Boolean) derives ReadWriter