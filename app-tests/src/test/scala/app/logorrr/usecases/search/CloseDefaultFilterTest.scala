package app.logorrr.usecases.search

import app.logorrr.TestFiles
import app.logorrr.model.LogFileSettings
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.RemoveFilterbutton
import app.logorrr.views.text.LogTextView
import org.junit.jupiter.api.Test

import java.nio.file.Files

class CloseDefaultFilterTest extends SingleFileApplicationTest(TestFiles.simpleLog1) {

  @Test def closeDefaultFilter(): Unit = {
    openFile(fileId)
    val lines = Files.lines(fileId.asPath).count

    LogFileSettings.DefaultFilters.foreach {
      f => waitAndClickVisibleItem(RemoveFilterbutton.uiNode(fileId, f))
    }

    waitForVisibility(LogTextView.uiNode(fileId))

    val res = lookup(LogTextView.uiNode(fileId).ref).query[LogTextView]

    assert(res.getItems.size == lines) // all lines are shown
  }

}
