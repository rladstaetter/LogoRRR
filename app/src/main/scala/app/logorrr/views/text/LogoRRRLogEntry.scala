package app.logorrr.views.text

import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import app.logorrr.views.search.Filter
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.paint.Color

import scala.collection.mutable.ListBuffer

object LogoRRRLogEntry {


  def calcParts(str: String
                , searchTerm: String
                , color: Color): Seq[LinePart] = {
    if (str.length < searchTerm.length) {
      Seq()
    } else {
      var lastIndex = 0
      var toSearch = str
      val bf = new ListBuffer[LinePart]
      while (toSearch.nonEmpty) {
        val o = toSearch.indexOf(searchTerm)
        if (o != -1) {
          bf.append(LinePart(searchTerm, lastIndex + o, Option(color)))
          lastIndex = lastIndex + o + searchTerm.length
          toSearch = toSearch.substring(o + searchTerm.length, toSearch.length)
        } else {
          toSearch = ""
        }
      }
      bf.toSeq
    }
  }


  def digest2(string: String, parts: Seq[LinePart]): Seq[Label] = {
    if (parts.isEmpty) {
      Seq(new Label(string))
    } else {
      val sorted = parts.sortWith((a, b) => a.start < b.start).distinct
      //  val sorted = parts.sortWith((a, b) => a.start < b.start)
      val labels =
        for ((s, i) <- string.zipWithIndex) yield {
          val relevant = sorted.filter(p => p.start <= i && i < p.end && p.someColor.isDefined)
          val l = new Label(s.toString)
          if (relevant.nonEmpty) {
            if (relevant.size == 1) {
              LogoRRRLabel.mkL(s.toString, relevant.head.someColor)
            } else {
              val cols = relevant.flatMap(_.someColor)
              LogoRRRLabel.mkL(s.toString, Option(cols.foldLeft(cols.head)((acc, sf) => acc.interpolate(sf, 0.5))))
            }
          } else {
            LogoRRRLabel.mkL(s.toString)
          }
        }

      labels
    }
  }

}

case class LogoRRRLogEntry(e: LogEntry
                           , maxLength: Int
                           , filters: Seq[Filter]
                        ) extends HBox with CanLog {
  /*
  private val llabels: Seq[Label] =
    for ((c, i) <- Seq(e.value, e.value, e.value).zipWithIndex) yield {
      val l = new Label(c)
      if (i % 2 == 0) {
        l.setBackground(bg)
        l.setTextFill(Color.BLACK)
      }
      l.setStyle("-fx-background: rgb(255,0,255);")
       l.setTextFill(Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255)))
      l
    }
*/

  lazy val res ={
    val lineParts =
      (for (f <- filters) yield {
        LogoRRRLogEntry.calcParts(e.value, f.pattern, f.color)
      }).flatten
    LinePart.reduce(e.value, lineParts).map(p => LogoRRRLabel.mkL(p.value, p.someColor))
  }

  //BorderPane.setAlignment(res, Pos.CENTER_LEFT)

  val lineNumberLabel: LineNumberLabel = LineNumberLabel(e.lineNumber, maxLength)
  getChildren.add(lineNumberLabel)
  getChildren.addAll(res: _*)
  //getChildren.addAll(new Label(e.value))


}