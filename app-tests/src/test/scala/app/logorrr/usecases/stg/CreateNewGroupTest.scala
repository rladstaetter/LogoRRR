package app.logorrr.usecases.stg

import app.logorrr.TestFiles
import app.logorrr.usecases.SingleFileApplicationTest
import org.junit.jupiter.api.Test

/**
 * Shows that adding a new search term group via the search term group editor works.
 */
class CreateNewGroupTest extends SingleFileApplicationTest(TestFiles.simpleLog0) with StgEditorActions:

  @Test def createNewGroupAndTestChoiceBox(): Unit =
    addGroup("Test Group")


