package app.logorrr.issues

import app.logorrr.TestFiles
import app.logorrr.conf.Settings.calcDefaultScreenPosition
import app.logorrr.conf.{BlockSettings, Settings, StageSettings}
import app.logorrr.model.LogFileSettings
import app.logorrr.steps.ChunkListViewActions
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.st.SearchTermButton
import app.logorrr.views.search.{MutableSearchTerm, SearchTerm}
import javafx.scene.paint.Color
import org.junit.jupiter.api.Test

/**
 * https://github.com/rladstaetter/LogoRRR/issues/292
 *
 * Color calculation test:
 *
 * - checks if color in chunklistview is calculated correctly by
 * - using three searchterms with given colors (via settings)
 * - checking color of element of chunklistview
 * */
abstract class Issue292ColorCalculationSetup(desiredColor: Color, val searchTerms: Seq[SearchTerm]) extends SingleFileApplicationTest(TestFiles.simpleLog5)
  with ChunkListViewActions {


  /** setup settings such that the issue is triggered and can be inspected visually */
  override lazy val settings: Settings = Settings(
    StageSettings(calcDefaultScreenPosition())
    , Map(TestFiles.simpleLog5.value ->
      LogFileSettings(TestFiles.simpleLog5)
        .copy(
          searchTerms = searchTerms
          , blockSettings = BlockSettings(50)
          , dividerPosition = 0.599))
    , None
    , None
    , Map()
  )

  @Test def testIssue292(): Unit = {
    openFile(TestFiles.simpleLog5)
    val color = nthCell(lookupChunkListView(fileId), 0).view.getImage.getPixelReader.getColor(5, 5)
    assert(color == desiredColor, s"${color.toString} != ${desiredColor.toString}")
  }

}

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

  @Test override def testIssue292(): Unit = {
    openFile(TestFiles.simpleLog5)

    val searchTerm = MutableSearchTerm(searchTerms.head)
    clickAndCheckColor(searchTerm, Color.web("0x00407fff"))
    clickAndCheckColor(searchTerm, Color.web("0x552a55ff"))

  }

  private def clickAndCheckColor(searchTerm: MutableSearchTerm, desiredColor: Color): Unit = {
    waitAndClickVisibleItem(SearchTermButton.uiNode(fileId, searchTerm))
    val color = nthCell(lookupChunkListView(fileId), 0).view.getImage.getPixelReader.getColor(5, 5)
    assert(color == desiredColor, s"${color.toString} != ${desiredColor.toString}")
  }
}