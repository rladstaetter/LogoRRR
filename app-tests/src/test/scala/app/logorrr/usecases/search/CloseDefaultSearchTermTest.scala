package app.logorrr.usecases.search

import app.logorrr.TestFiles
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.search.st.RemoveSearchTermButton
import app.logorrr.views.text.LogTextView
import org.junit.jupiter.api.Test

import java.nio.file.Files

class CloseDefaultSearchTermTest extends SingleFileApplicationTest(TestFiles.simpleLog1) {

  @Test def closeDefaultFilter(): Unit = {
    openFile(fileId)
    val lines = Files.lines(fileId.asPath).count

    MutableSearchTerm.DefaultSearchTerms.foreach {
      f => waitAndClickVisibleItem(RemoveSearchTermButton.uiNode(fileId, f))
    }

    waitForVisibility(LogTextView.uiNode(fileId))

    val res = lookup(LogTextView.uiNode(fileId).ref).query[LogTextView]

    assert(res.getItems.size == lines) // all lines are shown
  }

}
