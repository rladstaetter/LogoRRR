package app.logorrr.issues

import app.logorrr.TestFiles
import app.logorrr.conf.{BlockSettings, LogFileSettings, Settings, StageSettings}
import app.logorrr.steps.{LogTextViewActions, TestFxListViewActions}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.util.JfxUtils
import org.junit.jupiter.api.Test

/**
 * https://github.com/rladstaetter/LogoRRR/issues/294
 *
 * Clicking on ChunkListView yielded more than one selection in LogTextView. This
 * test shows that this is not the case anymore.
 *
 * */
class Issue294SelectElementInChunkListActivatesOnlyOneElementInTextViewTest
  extends SingleFileApplicationTest(TestFiles.simpleLog1)
    with LogTextViewActions
    with TestFxListViewActions:

  /** setup settings such that the issue is triggered and can be inspected visually */
  override lazy val settings: Settings = Settings(
    StageSettings(JfxUtils.calcDefaultScreenPosition())
    , Map(TestFiles.simpleLog1.value ->
      LogFileSettings.mk(TestFiles.simpleLog1)
        .copy(
          blockSettings = BlockSettings(50)
          , dividerPosition = 0.599))
    , None
    , None
    , Map()
  )

  // atm this is only a setup test which helps to get LogoRRR in a repeatable, defined state
  // start LogoRRRApp afterwards to tinker around
  @Test def testIssue294(): Unit =

    val ltv = lookupLogTextView(fileId)

    assert(ltv.getSelectionModel.getSelectedItems.size() == 0)

    // simple log is shown.
    val clv = lookupChunkListView(fileId)

    val firstCell = nthCell(clv, 0)

    val imageView = firstCell.view

    val imageViewBounds = imageView.localToScreen(imageView.getBoundsInLocal)
    val imageViewX = imageViewBounds.getMinX
    val imageViewY = imageViewBounds.getMinY

    val localX = 15.0
    val localY = 25.0

    val screenClickX = imageViewX + localX
    val screenClickY = imageViewY + localY

    clickOn(screenClickX, screenClickY)

    // assert that now one element is selected
    assert(ltv.getSelectionModel.getSelectedItems.size() == 1)

    // click on another element
    clickOn(screenClickX + 100, screenClickY)

    // assert that still only one element is selected (as opposed to more than one)
    assert(ltv.getSelectionModel.getSelectedItems.size() == 1)



