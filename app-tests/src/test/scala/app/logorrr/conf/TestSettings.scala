package app.logorrr.conf

import app.logorrr.cp.TxtCp
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.MutableSearchTerm
import javafx.geometry.Rectangle2D

object TestSettings {

  val Default = "Default"

  val cpResource = TxtCp("/app/logorrr/conf/test-search-term-groups.json")

  val DefaultGroups = DefaultSearchTermGroups(cpResource)

  val DefaultSearchTerms: Seq[MutableSearchTerm] = DefaultGroups.searchTermGroups(1).terms.map(MutableSearchTerm.apply)

  val Groups: Seq[SearchTermGroup] = DefaultGroups.searchTermGroups

  // tests are run headless per default, but this helps with debugging
  lazy val stageArea: Rectangle2D = {
    val isHeadless = Option(System.getProperty("glass.platform")).exists(_.toLowerCase() == "headless")
    if isHeadless then
      new Rectangle2D(0, 0, 1728.0, 898.0) // values of my setup which makes it easier to correlate headless tests to 'normal' tests
    else JfxUtils.calcDefaultScreenPosition()
  }

  lazy val DefaultSettings: Settings = Settings(
    StageSettings(stageArea)
    , Map()
    , None
    , None
    , Groups
    , None
  )


}
