package app.logorrr.views.text

import app.logorrr.model.LogEntry
import app.logorrr.views.search.MutableSearchTerm
import javafx.scene.paint.Color

import scala.collection.mutable.ListBuffer

object SearchTermCalculator:

  def apply(logEntry: LogEntry
            , searchTerms: Seq[MutableSearchTerm]): SearchTermCalculator =
    val logLine = logEntry.value
    val filters: Seq[(String, Color)] = searchTerms.filter(_.isActive).map(s => (s.getValue, s.getColor))
    SearchTermCalculator(logLine, filters)


/**
 * For each log entry, calculate correct colors for each char displayed and also the mean color for this entry
 *
 * @param logLine     string to colorize
 * @param searchTerms filter containing color and search string
 */
case class SearchTermCalculator(logLine: String
                                , searchTerms: Seq[(String, Color)]):


  lazy val filteredParts: Seq[Seq[LinePart]] = searchTerms.map((v, c) => calcParts(v, c))

  /**
   * For a given [[logLine]], compute the labels and associated colors which make up a displayed log line in the
   * user interface.
   * */
  lazy val stringColorPairs: Seq[(String, Color)] = calcStringColorPairs


  /**
   * For a given string and a search pattern, return all subindices of this string
   * where this search pattern matches.
   *
   * If either logLine or searchPattern are empty then return an empty Seq. If searchpattern length is
   * greater than [[logEntry]] length then return the empty list.
   *
   * @param searchPattern a pattern
   * @param color         color to apply
   * @return
   */
  private def calcParts(searchPattern: String
                        , color: Color): Seq[LinePart] =
    if searchPattern.isEmpty || logLine.isEmpty then
      Seq()
    else if logLine.length < searchPattern.length then
      Seq()
    else
      var currentIndex = 0
      val bf = new ListBuffer[LinePart]
      while currentIndex <= (logLine.length - searchPattern.length) do
        val lSearch = logLine.substring(currentIndex, logLine.length)
        val o = lSearch.indexOf(searchPattern)
        if o != -1 then
          val part = LinePart(searchPattern, currentIndex + o, color)
          bf.append(part)
          currentIndex = currentIndex + o + searchPattern.length
        else
          // end while loop
          currentIndex = logLine.length
      bf.toSeq

  private def calcStringColorPairs: Seq[(String, Color)] =
    val value = logLine
    // if there are no filters, it is easy - just return the whole string with special color
    if filteredParts.isEmpty then
      Seq((value, MutableSearchTerm.UnclassifiedColor))
    else
      // brute force:
      // for all filters, calculate if there is a hit or not.
      // if yes, provide the color for this filter, if not just None
      val jou: Seq[IndexedSeq[Option[Color]]] =
        for pts <- filteredParts yield
          for i <- 0 to value.length yield
            pts.find(p => p.hit(i)) match
              case Some(value) => Option(value.color)
              case None => None

      // reduce all colors:
      // we have a Seq[Seq[Option[Color]]] and want a Seq[Color] in the end.
      // as intermediate step, calculate a Seq[Option[Color]] by interpolating
      // all colors for each filter.
      val cols: Seq[Option[Color]] =
        for i <- 0 to value.length yield
          val colors: Seq[Option[Color]] = for j <- jou yield j(i)
          colors.tail.foldLeft(colors.head)((acc, c) => {
            (acc, c) match {
              case (None, None) => None
              case (None, Some(v)) => Option(v)
              case (Some(c), None) => Option(c)
              case (Some(aC), Some(c)) => Option(aC.interpolate(c, 0.5))
            }
          })

      // almost there, now we have already a Seq[(String,Color)]
      // but the caveat is that we produce many single labels in the end which
      // is too slow. We try to reduce adjacent labels with the same color which
      // will typically result in a considerable smaller size of this sequence.
      val valuesForEveryChar: Seq[(Char, Option[Color])] = value zip cols

      // crunch sequence and concatenate entries with same colors
      valuesForEveryChar.foldLeft(Seq[(String, Color)]())({
        case (acc, (c, someCol)) =>
          val curColor =
            someCol match {
              case Some(value) => value
              case None => MutableSearchTerm.UnclassifiedColor
            }
          // handle special case for first element
          if acc.isEmpty then {
            Seq((c.toString, curColor))
          } else {
            // get last entry
            val (entry, lastColor) = acc.last
            // colors are the same - drop last element from sequence, add new element with mutated entry
            if lastColor == curColor then {
              acc.dropRight(1) :+ (entry + c, curColor)
            } else {
              // just add new entry to acc
              acc :+ (c.toString, curColor)
            }
          }
      })


