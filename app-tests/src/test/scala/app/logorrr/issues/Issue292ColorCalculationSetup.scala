package app.logorrr.issues

import app.logorrr.TestFiles
import app.logorrr.conf.*
import app.logorrr.steps.{SearchTermToolbarActions, TestFxListViewActions}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.util.JfxUtils
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
abstract class Issue292ColorCalculationSetup(desiredColor: Color
                                             , val searchTerms: Seq[SearchTerm])
  extends SingleFileApplicationTest(TestFiles.simpleLog5)
    with TestFxListViewActions
    with SearchTermToolbarActions:

  /** setup settings such that the issue is triggered and can be inspected visually */
  override lazy val settings: Settings = Settings(
    StageSettings(JfxUtils.calcDefaultScreenPosition())
    , Map(TestFiles.simpleLog5.value ->
      LogFileSettings.mk(TestFiles.simpleLog5, TestSettings.DefaultGroups.searchTermGroups.tail.head)
        .copy(
          searchTerms = searchTerms
          , blockSize= 50
          , dividerPosition = 0.599))
    , None
    , None
    , Seq(SearchTermGroup(getClass.getSimpleName, searchTerms, true)) // set as default search term group
    , None
  )

  @Test def testIssue292(): Unit =
    openFile(TestFiles.simpleLog5)
    val color = nthCell(lookupChunkListView(fileId), 0).view.getImage.getPixelReader.getColor(5, 5)
    assert(color == desiredColor, s"${color.toString} != ${desiredColor.toString}")
