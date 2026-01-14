package app.logorrr.conf

import app.logorrr.cp.TxtCp
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.MutableSearchTerm

object TestSettings {

  val cpResource = TxtCp("/app/logorrr/conf/test-search-term-groups.json")

  val groups = DefaultSearchTermGroups(cpResource)

  val DefaultSearchTerms: Seq[MutableSearchTerm] = DefaultSearchTermGroups().jul.terms.map(MutableSearchTerm.apply)

  lazy val Default: Settings = Settings(
    StageSettings(JfxUtils.calcDefaultScreenPosition())
    , Map()
    , None
    , None
    , groups.searchTermGroups.map(stg => stg.name -> stg.terms).toMap
  )


}
