package app.logorrr.usecases.search

import app.logorrr.TestFiles
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.MutFilter
import app.logorrr.views.search.FilterButton
import app.logorrr.views.search.filter.UnclassifiedFilter
import app.logorrr.views.text.LogTextView
import org.junit.jupiter.api.Test

/**
 * enables/disables specific filters
 * */
class SelectDefaultSearchTermTest extends SingleFileApplicationTest(TestFiles.simpleLog2) {

  @Test def selectSpecificFilter(): Unit = {
    // file has 5 entries - each line consisting of one entry corresponding to
    // the default filters, like 'FINEST', 'INFO, ' WARNING', 'SEVERE'
    // and a 'unclassified' line
    openFile(fileId)

    MutFilter.DefaultFilters.foreach {
      f =>
        // deselect all filters except unclassified
        clickFilters(MutFilter.DefaultFilters)

        // now, only 'unclassified' filter is active. since there are no unclassified
        // entries available, the number of displayed log entries is one
        checkNumberOfShownElements(1)

        // select one specific filter- now two lines are shown in total
        waitAndClickVisibleItem(FilterButton.uiNode(fileId, f))
        checkNumberOfShownElements(2)

        // deselect filter again
        waitAndClickVisibleItem(FilterButton.uiNode(fileId, f))
        clickFilters(MutFilter.DefaultFilters)
    }

    // finally, deselect all filters
    clickFilters(MutFilter.DefaultFilters)
    // one entry is shown (unclassified)
    checkNumberOfShownElements(1)

    // deselect unclassified filter works as well
    clickFilters(Seq(UnclassifiedFilter(MutFilter.DefaultFilters.toSet)))
    checkNumberOfShownElements(0)
  }

  def checkNumberOfShownElements(expectedElements: Int): Unit = {
    assert(lookup(LogTextView.uiNode(fileId).ref).query[LogTextView].getItems.size() == expectedElements)
  }

  def clickFilters(filters: Seq[MutFilter]): Unit = {
    filters.foreach {
      ff => waitAndClickVisibleItem(FilterButton.uiNode(fileId, ff)) // enable all filters
    }
  }

}
