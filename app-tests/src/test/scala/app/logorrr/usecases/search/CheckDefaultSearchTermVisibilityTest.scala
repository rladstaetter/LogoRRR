package app.logorrr.usecases.search

import app.logorrr.TestFiles
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.search.st.SearchTermButton
import org.junit.jupiter.api.Test

/**
 * on startup/opening a file, certain filters should be visible per default
 * */
class CheckDefaultSearchTermVisibilityTest extends SingleFileApplicationTest(TestFiles.simpleLog1):

  @Test def checkIfDefaultFiltersAreActive(): Unit =
    openFile(fileId)

    MutableSearchTerm.DefaultSearchTerms.foreach:
      f => waitForVisibility(SearchTermButton.uiNode(fileId, f))

