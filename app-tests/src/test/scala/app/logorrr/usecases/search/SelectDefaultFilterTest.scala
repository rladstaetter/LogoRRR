package app.logorrr.usecases.search

import app.logorrr.TestFiles
import app.logorrr.model.LogFileSettings
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.FilterButton
import app.logorrr.views.text.LogTextView
import org.junit.jupiter.api.Test

/**
 * enables/disables specific filters
 * */
class SelectDefaultFilterTest extends SingleFileApplicationTest(TestFiles.simpleLog2) {

  @Test def selectSpecificFilter(): Unit = {
    openFile(path)

    LogFileSettings.DefaultFilters.foreach {
      f =>
        LogFileSettings.DefaultFilters.foreach {
          ff => waitAndClickVisibleItem(FilterButton.uiNode(ff)) // disable all filters
        }
        waitAndClickVisibleItem(FilterButton.uiNode(f)) // enable one specific filer

        val res = lookup(LogTextView.uiNode(fileId).ref).query[LogTextView]
        assert(res.getItems.size() == 1)
        waitAndClickVisibleItem(FilterButton.uiNode(f)) // enable disable specific filer

        LogFileSettings.DefaultFilters.foreach {
          ff => waitAndClickVisibleItem(FilterButton.uiNode(ff)) // enable all filters
        }
    }
  }

}
