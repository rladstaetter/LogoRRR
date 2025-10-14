package app.logorrr.views.search.stg

import app.logorrr.views.search.SearchTerm
import javafx.scene.paint.Color


object StgEntry {

  val Empty: StgEntry = StgEntry("empty", Seq())

  val Default: StgEntry = StgEntry("default", Seq(
    SearchTerm("FINEST", Color.GREY, active = true)
    , SearchTerm("INFO", Color.GREEN, active = true)
    , SearchTerm("WARNING", Color.ORANGE, active = true)
    , SearchTerm("SEVERE", Color.RED, active = true)
  ))

  def mkSearchTermGroups: Map[String, Seq[SearchTerm]] = Seq(Default, Empty).map(stg => stg.name -> stg.terms).toMap
}

case class StgEntry(name: String, terms: Seq[SearchTerm])
