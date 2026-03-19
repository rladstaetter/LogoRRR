package app.logorrr.issues

import app.logorrr.TestFiles
import app.logorrr.conf.{BlockSettings, DefaultSearchTermGroups, LogFileSettings, SearchTerm, Settings, StageSettings, TestSettings}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.st.ASearchTermToggleButton
import app.logorrr.views.search.MutableSearchTerm
import javafx.scene.paint.Color
import org.junit.jupiter.api.Test




class Issue292SimpleColorTest extends Issue292ColorCalculationSetup(Color.RED, Seq(SearchTerm("aaaaaa", Color.RED, active = true)))

class Issue292DualColorTest extends Issue292ColorCalculationSetup(Color.web("0x7f7f7fff"),
  Seq(
    SearchTerm("aaaaaa", Color.color(0.0, 0.0, 0.0), active = true)
    , SearchTerm("bbbbbb", Color.color(1.0, 1.0, 1.0), active = true)
  ))

class Issue292DualColorInactiveTest extends Issue292ColorCalculationSetup(Color.web("0x7f7f7fff"),
  Seq(
    SearchTerm("aaaaaa", Color.color(0.0, 0.0, 0.0), active = true)
    , SearchTerm("bbbbbb", Color.color(1.0, 1.0, 1.0), active = true)
    , SearchTerm("cccccc", Color.color(1.0, 1.0, 1.0), active = false)
  ))

class Issue292TripleColorTest extends Issue292ColorCalculationSetup(Color.web("0x552a55ff"),
  Seq(
    SearchTerm("aaaaaa", Color.RED, active = true)
    , SearchTerm("bbbbbb", Color.GREEN, active = true)
    , SearchTerm("cccccc", Color.BLUE, active = true)
  ))


/**
 * tests that enabling/disabling of the searchterms yields different colors
 */
class Issue292TripleColorWithDeactivationTest extends Issue292ColorCalculationSetup(Color.web("0x552a55ff"),
  Seq(
    SearchTerm("aaaaaa", Color.RED, active = true)
    , SearchTerm("bbbbbb", Color.GREEN, active = true)
    , SearchTerm("cccccc", Color.BLUE, active = true)
  )) {

  @Test override def testIssue292(): Unit =
    openFile(TestFiles.simpleLog5)

    val searchTerm = MutableSearchTerm(searchTerms.head)
    clickAndCheckColor(searchTerm, Color.web("0x00407fff"))
    clickAndCheckColor(searchTerm, Color.web("0x552a55ff"))


  private def clickAndCheckColor(searchTerm: MutableSearchTerm, desiredColor: Color): Unit =
    waitAndClickVisibleItem(ASearchTermToggleButton.uiNode(fileId, searchTerm.getValue))
    val color = nthCell(lookupChunkListView(fileId), 0).view.getImage.getPixelReader.getColor(5, 5)
    assert(color == desiredColor, s"${color.toString} != ${desiredColor.toString}")
}