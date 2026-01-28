package app.logorrr.conf

import app.logorrr.cp.TxtCp
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.MutableSearchTerm

object TestSettings {

  val Java_JUL = "Java_JUL"
  
  val cpResource = TxtCp("/app/logorrr/conf/test-search-term-groups.json")

  val DefaultGroups = DefaultSearchTermGroups(cpResource)

  val DefaultSearchTerms: Seq[MutableSearchTerm] = DefaultGroups.getTerms(Java_JUL).get.map(MutableSearchTerm.apply)

  val DefaultGroupsAsMap: Map[String, Seq[SearchTerm]] = DefaultGroups.searchTermGroups.map(stg => stg.name -> stg.terms).toMap

  lazy val Default: Settings = Settings(
    StageSettings(JfxUtils.calcDefaultScreenPosition())
    , Map()
    , None
    , None
    , DefaultGroupsAsMap
    , None
  )


}
