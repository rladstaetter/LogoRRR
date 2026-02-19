package app.logorrr.conf

import app.logorrr.cp.TxtCp
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.MutableSearchTerm

object TestSettings {

  val Default = "Default"

  val cpResource = TxtCp("/app/logorrr/conf/test-search-term-groups.json")

  val DefaultGroups = DefaultSearchTermGroups(cpResource)

  val DefaultSearchTerms: Seq[MutableSearchTerm] = DefaultGroups.searchTermGroups(1).terms.map(MutableSearchTerm.apply)

  val Groups: Seq[SearchTermGroup] = DefaultGroups.searchTermGroups

  lazy val DefaultSettings: Settings = Settings(
    StageSettings(JfxUtils.calcDefaultScreenPosition())
    , Map()
    , None
    , None
    , Groups
    , None
  )


}
