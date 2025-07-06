package app.logorrr.usecases.search

import app.logorrr.TestFiles
import app.logorrr.jfxbfr.MutFilter
import app.logorrr.model.FilterUtil
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.FilterButton
import app.logorrr.views.search.filter.UnclassifiedFilter
import app.logorrr.views.text.LogTextView
import org.junit.jupiter.api.Test

/**
 * enables/disables specific filters
 * */
class SelectDefaultFilterTest extends SingleFileApplicationTest(TestFiles.simpleLog2) {

  @Test def selectSpecificFilter(): Unit = {
    // file has 5 entries - each line consisting of one entry corresponding to
    // the default filters, like 'FINEST', 'INFO, ' WARNING', 'SEVERE'
    // and a 'unclassified' line
    openFile(fileId)

    FilterUtil.DefaultFilters.foreach {
      f =>
        // deselect all filters except unclassified
        clickFilters(FilterUtil.DefaultFilters)

        // now, only 'unclassified' filter is active. since there are no unclassified
        // entries available, the number of displayed log entries is one
        checkNumberOfShownElements(1)

        // select one specific filter- now two lines are shown in total
        waitAndClickVisibleItem(FilterButton.uiNode(fileId, f))
        checkNumberOfShownElements(2)

        // deselect filter again
        waitAndClickVisibleItem(FilterButton.uiNode(fileId, f))
        clickFilters(FilterUtil.DefaultFilters)
    }

    // finally, deselect all filters
    clickFilters(FilterUtil.DefaultFilters)
    // one entry is shown (unclassified)
    checkNumberOfShownElements(1)

    // deselect unclassified filter works as well
    clickFilters(Seq(UnclassifiedFilter(FilterUtil.DefaultFilters.toSet)))
    checkNumberOfShownElements(0)
  }

  def checkNumberOfShownElements(expectedElements: Int): Unit = {
    assert(lookup(LogTextView.uiNode(fileId).ref).query[LogTextView].getItems.size() == expectedElements)
  }

  def clickFilters(filters: Seq[MutFilter[_]]): Unit = {
    filters.foreach {
      ff => waitAndClickVisibleItem(FilterButton.uiNode(fileId, ff)) // enable all filters
    }
  }

}
