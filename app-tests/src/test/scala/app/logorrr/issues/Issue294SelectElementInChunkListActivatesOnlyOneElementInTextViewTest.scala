package app.logorrr.issues

import app.logorrr.TestFiles
import app.logorrr.clv.ChunkListCell
import app.logorrr.conf.Settings.calcDefaultScreenPosition
import app.logorrr.conf.{BlockSettings, Settings, StageSettings}
import app.logorrr.model.{LogEntry, LogFileSettings}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.logfiletab.LogoRRRChunkListView
import app.logorrr.views.text.LogTextView
import org.junit.jupiter.api.Test

/**
 * https://github.com/rladstaetter/LogoRRR/issues/294
 *
 * Clicking on ChunkListView yielded more than one selection in LogTextView. This
 * test shows that this is not the case anymore.
 *
 * */
class Issue294SelectElementInChunkListActivatesOnlyOneElementInTextViewTest
  extends SingleFileApplicationTest(TestFiles.simpleLog1) {

  /** setup settings such that the issue is triggered and can be inspected visually */
  override lazy val settings: Settings = Settings(
    StageSettings(calcDefaultScreenPosition())
    , Map(TestFiles.simpleLog1.value ->
      LogFileSettings(TestFiles.simpleLog1)
        .copy(
          blockSettings = BlockSettings(50)
          , dividerPosition = 0.599))
    , None
    , None
  )

  // atm this is only a setup test which helps to get LogoRRR in a repeatable, defined state
  // start LogoRRRApp afterwards to tinker around
  @Test def testIssue294(): Unit = {

    // check that no text view element is selected:

    val logTextViewUiElem = LogTextView.uiNode(fileId)
    val ltv = lookup(logTextViewUiElem.ref).query[LogTextView]

    assert(ltv.getSelectionModel.getSelectedItems.size() == 0)

    // simple log is shown.
    val chunkListViewUiElem = LogoRRRChunkListView.uiNode(fileId)
    val clv = lookup(chunkListViewUiElem.ref).query[LogoRRRChunkListView]

    val firstCell = from(clv).lookup(".list-cell").nth(0).query[ChunkListCell[LogEntry]]()

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


  }

}
