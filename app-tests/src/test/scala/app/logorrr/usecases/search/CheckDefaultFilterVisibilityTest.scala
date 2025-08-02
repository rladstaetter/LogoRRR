package app.logorrr.usecases.search

import app.logorrr.TestFiles
import app.logorrr.model.FilterUtil
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.FilterButton
import org.junit.jupiter.api.Test

/**
 * on startup/opening a file, certain filters should be visible per default
 * */
class CheckDefaultFilterVisibilityTest extends SingleFileApplicationTest(TestFiles.simpleLog1) {

  @Test def checkIfDefaultFiltersAreActive(): Unit = {
    openFile(fileId)

    FilterUtil.DefaultFilters.foreach {
      f => waitForVisibility(FilterButton.uiNode(fileId, f))
    }
  }

}
