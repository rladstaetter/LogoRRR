package app.logorrr.usecases.search

import app.logorrr.TestFiles
import app.logorrr.steps.SearchTermToolbarActions
import app.logorrr.usecases.SingleFileApplicationTest
import org.junit.jupiter.api.Test

/**
 * Issues some queries and checks if search term toggle button exists
 */
class SimpleSearchTest extends SingleFileApplicationTest(TestFiles.simpleLog0) with SearchTermToolbarActions:

  @Test def searchAndCheckToggleButtons(): Unit =
    openFile(fileId)

    Seq("1", "2", "3").foreach:
      s =>
        search(fileId, s)
        existsSearchTermToggleButton(fileId, s)

